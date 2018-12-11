package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

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

	MoneyRegister register;
	private Customer c;

	public SellingService(Customer c) {
		super("sellingService");
		register=MoneyRegister.getInstance();
		this.c=c;
	}

	@Override
	protected void initialize() {
		Callback<BookOrderEvent> order = (e) -> {
			Future<Boolean> isInInv= super.sendEvent(new CheckBookAvailabiltyEvent());
			while (!isInInv.isDone())
				try {
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			if (isInInv.get()) {
				CheckBankAccountEvent checkAcc= new CheckBankAccountEvent(e.getPrice());
				Future<Boolean> has$ = super.sendEvent(checkAcc);//@TODO: check for diarrhea leaks
				while(!has$.isDone())
					try {
						checkAcc.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				if (has$.get()){
					register.chargeCreditCard(c, e.getPrice());
					sendEvent(new DeliveryEvent());
					sendEvent(new CompleteOrderEvent(this));
				}
				else
					sendEvent(new CancelOrderEvent());
			}
			else
				sendEvent(new CancelOrderEvent());
		};
		subscribeEvent(BookOrderEvent.class, order);
	}

}
