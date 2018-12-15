package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;
	private boolean isInitialized;

	public InventoryService(String name) {
		super(name);
		inventory= Inventory.getInstance();
		isInitialized = false;
	}

	@Override
	protected void initialize() {
		// terminate this when received
		subscribeBroadcast(TerminateAllBroadcast.class, t -> terminate());

		Callback<CheckAvailabilityEvent> check = (checkAvailabilityEvent)-> {
			int price = inventory.checkAvailabiltyAndGetPrice(checkAvailabilityEvent.getName());
			complete(checkAvailabilityEvent, price);
		};
		subscribeEvent(CheckAvailabilityEvent.class, check);

		// removing a book from the inventory
		Callback<TakeFromInventoryEvent> take = (takeFromInventoryEvent) ->
				inventory.take(takeFromInventoryEvent.getBookTitle());
		subscribeEvent(TakeFromInventoryEvent.class, take);

		// signaling that the Micro Service has initialized
		isInitialized = true;
	}
	public boolean isInitialized() { return isInitialized; }
}