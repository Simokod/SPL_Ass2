package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.*;

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
	int orderId;					//TODO: move to main
	LinkedList<BookInventoryInfo> orderList;

	public APIService(String name, Customer c, int orderId, LinkedList<BookInventoryInfo> orderList) {
		super(name);
		this.c = c;
		this.orderId=orderId;
		this.orderList=orderList;
	}

	@Override
	protected void initialize() {
		//subscribeBroadcast(TimeTick.class, timeCB -> () );

		subscribeEvent(CancelOrderEvent.class, cancel -> {/**/}) ;	// TODO: fix this shit

		for (BookInventoryInfo book:orderList) {
			sendEvent(new BookOrderEvent(book));
		}

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
			//sendEvent(new DeliveryEvent(c.getAddress(),c.getDistance()));
		};
		subscribeEvent(CompleteOrderEvent.class, complete);
	}
	// Sends BookOrderEvent to the MessageBus and expects an OrderReceipt
}
