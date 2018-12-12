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

	Inventory inventory;

	public InventoryService(Inventory inv) {
		super("InventoryService");
		inventory= Inventory.getInstance();
	}

	@Override
	protected void initialize() {
		Callback<CheckAvailabilityEvent> check= (checkAvailabilityEvent)-> {
			int price = inventory.checkAvailabiltyAndGetPrice(checkAvailabilityEvent.getName());
			if (price != -1) {
				CheckBankAccountEvent checkAcc = new CheckBankAccountEvent(price);
				Future<Boolean> has$ = sendEvent(checkAcc);			//@TODO: check for diarrhea leaks
				if(has$.get()){
					if(inventory.take(checkAvailabilityEvent.getName())== OrderResult.SUCCESSFULLY_TAKEN)
						complete(checkAvailabilityEvent, price);
					else
						complete(checkAvailabilityEvent, -1);
				}
				else
					complete(checkAvailabilityEvent, -1);
			}
			else
				complete(checkAvailabilityEvent, -1);
		};
		subscribeEvent(CheckAvailabilityEvent.class, check);
	}
}
