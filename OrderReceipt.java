package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.services.SellingService;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	int orderId;
	BookInventoryInfo book;
	Customer customer;
	SellingService sellingService;
	/**
	 * Public Constructor
	 */
	public OrderReceipt(int orderId, Customer customer, BookInventoryInfo book, SellingService sellingService){
		this.orderId=orderId;
		this.customer=customer;
		this.book=book;
		this.sellingService=sellingService;
	}
	/**
	 * Retrieves the orderId of this receipt.
	 */
	public int getOrderId() { return orderId; }

	/**
	 * Retrieves the name of the selling service which handled the order.
	 */
	public String getSeller() { return sellingService.getName(); }

	/**
	 * Retrieves the ID of the customer to which this receipt is issued to.
	 * <p>
	 * @return the ID of the customer
	 */
	public int getCustomerId() { return customer.getId(); }

	/**
	 * Retrieves the name of the book which was bought.
	 */
	public String getBookTitle() { return book.getBookTitle(); }

	/**
	 * Retrieves the price the customer paid for the book.
	 */
	public int getPrice() { return book.getPrice(); }

	/**
	 * Retrieves the tick in which this receipt was issued.
	 */
	public int getIssuedTick() {
		// TODO Implement this
		return 0; }

	/**
	 * Retrieves the tick in which the customer sent the purchase request.
	 */
	public int getOrderTick() {
		// TODO Implement this
		return 0;	}

	/**
	 * Retrieves the tick in which the treating selling service started
	 * processing the order.
	 */
	public int getProcessTick() {
		// TODO Implement this
		return 0;
	}

	/**
	 * @return a String describing the object
	 */
	public String toString(){
		return "Order Id: " + orderId +", Book name: " + book.getBookTitle() + ", Customer's name: " + customer.getName();
	}
}
