package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService implements Serializable {

	private MoneyRegister moneyRegister;
	private int currentTick;
	private CountDownLatch latch;

	public SellingService(String name, CountDownLatch latch) {
		super(name);
		moneyRegister=MoneyRegister.getInstance();
		currentTick=0;
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		// updating time according to TimeService
		subscribeBroadcast(TimeTickBroadcast.class, time -> currentTick=time.getTime() );

		// terminate this when received
		subscribeBroadcast(TerminateAllBroadcast.class, t -> terminate());

		// checking if the book is in the inventory
		Callback<BookOrderEvent> order = (orderEvent) -> {
			int orderTick = orderEvent.getOrderTick();
			int processTick = currentTick;
			String bookTitle = orderEvent.getBook();
			Future<Integer> priceIfOrderPossible = sendEvent(new CheckAvailabilityEvent(bookTitle));
			// book is available
			if (priceIfOrderPossible.get()==null)
				return;
			int price = priceIfOrderPossible.get();
			if (price != -1) {		// enough money in the bank
				if(orderEvent.getCustomer().getAvailableCreditAmount()-price >= 0) {
					sendEvent(new TakeFromInventoryEvent(bookTitle));
					moneyRegister.chargeCreditCard(orderEvent.getCustomer(), price);
					CompleteOrderEvent completeOrder =
						new CompleteOrderEvent(this.getName(), bookTitle, orderEvent.getCustomer(), price, orderTick, processTick);
					Future<OrderReceipt> receipt = sendEvent(completeOrder);
					moneyRegister.file(receipt.get());
				}	// not enough money in bank
			}
			};
		subscribeEvent(BookOrderEvent.class, order);

		// signaling that the Micro Service has initialized
		latch.countDown();
	}
}
