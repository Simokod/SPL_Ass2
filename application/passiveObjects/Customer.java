package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String name;
	private int id;
	private String address;
	private int distance;
	private List<OrderReceipt> receiptList;
	private int availableCreditAmount;
	private int creditNumber;

	/**
	 * constructor for the Customer class
	 * @param name - Customer's name
	 * @param id - Customer's id
	 * @param address - Customer's address
	 * @param distance - Customer's distance from the store
	 * @param availableCreditAmount - Customer's amount of credit left
	 * @param creditNumber - Customer's credit card number
	 */
	public Customer(String name, int id, String address, int distance,
					int availableCreditAmount, int creditNumber) {
		this.name=name;
		this.id=id;
		this.address=address;
		this.distance=distance;
		this.availableCreditAmount=availableCreditAmount;
		this.creditNumber=creditNumber;
		this.receiptList=new LinkedList<>();
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return receiptList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return availableCreditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditNumber;
	}

	/**
	 * Charge the customer's credit card by amount
	 * <p>
	 * @param amount 	amount to charge
	 */
	public void chargeCreditCard(int amount){
		availableCreditAmount-=amount;
	}
}
