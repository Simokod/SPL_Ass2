package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.*;

import java.util.HashMap;
import java.util.LinkedList;

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
	private HashMap<String, Integer> orderList;
	private int orderId;

	public APIService(String name, int orderId, Customer c, HashMap<String, Integer> orderList) {
		super(name);
		this.c = c;
		this.orderList = orderList;
		this.orderId = orderId;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TimeTickBroadcast.class, timeCB -> {} );	// TODO: this also

		subscribeEvent(CancelOrderEvent.class, cancel -> {/**/}) ;	// TODO: fix this shit

		// sending Book Order Events    		TODO: check what about tick
		orderList.forEach((name,tick) -> sendEvent(new BookOrderEvent(name, c, tick)));

		//CheckBankAccountEvent handler
		Callback<CheckBankAccountEvent> checkBankAcc;
		checkBankAcc = (checkAccEvent) -> {
			complete(checkAccEvent, (c.getAvailableCreditAmount()-checkAccEvent.getPrice())>=0);
		};
		subscribeEvent(CheckBankAccountEvent.class, checkBankAcc);

		//CompleteOrderEvent handler
		Callback<CompleteOrderEvent> complete;
		complete = (completeOrderEvent) -> {
			OrderReceipt receipt= new OrderReceipt(orderId, c, completeOrderEvent.getBook(), completeOrderEvent.getSeller());
			c.getCustomerReceiptList().add(receipt);
			complete(completeOrderEvent, receipt);
			sendEvent(new DeliveryEvent(c.getAddress(),c.getDistance()));
		};
		subscribeEvent(CompleteOrderEvent.class, complete);
	}
	// Sends BookOrderEvent to the MessageBus and expects an OrderReceipt
}
