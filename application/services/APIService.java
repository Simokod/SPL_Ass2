package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

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
	private LinkedList<OrderPair> orderList;
	private AtomicInteger orderId;
	private int currentTick;
	private boolean isTimed;

	public APIService(String name, AtomicInteger orderId, Customer c, LinkedList<OrderPair> orderList) {
		super(name);
		this.c = c;
		this.orderList = orderList;
		this.orderId = orderId;
		this.isTimed = false;
	}

	@Override
	protected void initialize() {
		// updating time according to TimeService
		subscribeBroadcast(TimeTickBroadcast.class, timeBC -> {
			currentTick++;
			// sending Book Order Events
			for(OrderPair order: orderList)
				if(order.getOrderTick() == currentTick)
					sendEvent(new BookOrderEvent(order.getBookTitle(), c, currentTick));
		});
		isTimed = true;

		// canceling order because of inventory or bank account
		subscribeEvent(CancelOrderEvent.class, cancel -> {/**/}) ;	// TODO: fix this shit

		// CheckBankAccountEvent handler
		Callback<CheckBankAccountEvent> checkBankAcc = (checkAccEvent) ->
			complete(checkAccEvent, (c.getAvailableCreditAmount()-checkAccEvent.getPrice())>=0);

		subscribeEvent(CheckBankAccountEvent.class, checkBankAcc);

		// CompleteOrderEvent handler
		Callback<CompleteOrderEvent> complete = (completeOrderEvent) -> {
			// creating receipt
			String bookTitle = completeOrderEvent.getBook();
			String seller = completeOrderEvent.getSeller();
			int price = completeOrderEvent.getPrice();
			int orderTick = completeOrderEvent.getOrderTick();
			int proccessTick = completeOrderEvent.getProcessTick();
			OrderReceipt receipt= new OrderReceipt(orderId.getAndIncrement(), seller, c, bookTitle, price,
													currentTick, orderTick, proccessTick);

			synchronized (c) {
				c.getCustomerReceiptList().add(receipt);
			}
			complete(completeOrderEvent, receipt);
			sendEvent(new DeliveryEvent(c.getAddress(),c.getDistance()));
		};
		subscribeEvent(CompleteOrderEvent.class, complete);
	}
	public boolean isSubscribedToTime() { return isTimed; }
}
