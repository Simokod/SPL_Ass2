package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.io.Serializable;

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

	public SellingService() {
		super("sellingService");
		// TODO Implement this
	}

	@Override
	protected void initialize() {
		/*Callback<BookOrderEvent> order = (e) -> {
			Future<Boolean> isInInv= super.sendEvent(new CheckBookAvailabiltyEvent());
			while (!isInInv.isDone()){}		//@TODO: fix code barfing problem
			if (isInInv.get()) {
				Future<Boolean> has$ = super.sendEvent(new CheckBankAccountEvent(e.getPrice()));//@TODO: check for diarrhea leaks
				while(!has$.isDone()){}		//@TODO: fix code barfing problem
				if (has$.get()){
					super.sendEvent(new DeliveryEvent());
					super.sendEvent(new CompleteOrderEvent(*//*??*//*)); //@TODO: this.getShit();
				}
			}
				else
				super.sendEvent(new CancelOrderEvent());
		};
*/
	}
}
