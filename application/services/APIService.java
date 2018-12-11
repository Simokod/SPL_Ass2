package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.*;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	private Customer c;

	public APIService(String name, Customer c) {
		super(name);
		this.c = c;
	}

	@Override
	protected void initialize() {
		//subscribeBroadcast(TimeTick.class, timeCB -> () );
		Callback<CheckBankAccountEvent> checkBankAcc = (e) -> {
			if(c.getAvailableCreditAmount() >= e.price)
				c.chargeCreditCard(e.price);
				complete(e, (c.getAvailableCreditAmount()-e.price)>=0);
		};
		subscribeEvent(CheckBankAccountEvent.class, checkBankAcc);

		subscribeEvent(CancelOrderEvent.class, cancel -> {/*///////*/});		// TODO: fix this shit

		Callback<CompleteOrderEvent> complete = (e) -> {					// TODO: fix this shit also
			return e.receipt;
		};
		subscribeEvent(CompleteOrderEvent.class, complete);
	}
	// Sends BookOrderEvent to the MessageBus and expects an OrderReceipt
}
