package bgu.spl.mics.application.passiveObjects;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
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

	/**
	 * Private Constructor
	 */
	private MoneyRegister(){
		this.receiptList = new LinkedList<>();
		version=0;
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
	public void file (OrderReceipt r) {
		synchronized (this) {
			version++;
			receiptList.add(r);
		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int total;
		boolean tryAgain;
		final int originalVersion;
		synchronized (this) {
			originalVersion = version;
		}
		do {
			total= 0;
			Iterator it=receiptList.iterator();
			try {
				while(it.hasNext())
				{
					if(originalVersion!=version)
						throw new Exception();
					total += ((OrderReceipt) it.next()).getPrice();
				}
				tryAgain=false;
			}
			catch (Exception e) {
				tryAgain=true;
			}
		} while(tryAgain);
		return total;
	}

	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		if(c.getAvailableCreditAmount() >= amount)
			c.chargeCreditCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		synchronized (this) {
			try {
				FileOutputStream fileOut = new FileOutputStream((filename));
				ObjectOutputStream oos = new ObjectOutputStream(fileOut);
				oos.writeObject(receiptList);
				oos.close();
				fileOut.close();
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
}
