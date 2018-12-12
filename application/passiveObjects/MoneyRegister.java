package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {

	private static MoneyRegister instance = new MoneyRegister();
	private List<OrderReceipt> receiptList;
	private int version;
	private int totalEernings;

	/**
	 * Private Constructor
	 */
	private MoneyRegister(){
		this.receiptList = new LinkedList<>();
		version=0;
		totalEernings=0;
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {	return MoneyRegister.instance; }
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public synchronized void file (OrderReceipt r) { receiptList.add(r); }
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() { return totalEernings; }

	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public synchronized void chargeCreditCard(Customer c, int amount) {
		if(c.getAvailableCreditAmount() >= amount){
			c.chargeCreditCard(amount);
			totalEernings+=amount;
		}
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream((filename));
			ObjectOutputStream oos = new ObjectOutputStream(fileOut);
			oos.writeObject(receiptList);
			oos.close();
			fileOut.close();
		} catch (Exception e) { e.printStackTrace(); }
	}
}
