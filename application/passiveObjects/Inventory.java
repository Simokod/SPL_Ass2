package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private static Inventory instance = new Inventory();
	private HashMap<String, BookInventoryInfo> bookInventory;

	/**
	 *Constructor
	 */
	private Inventory() { this.bookInventory = new HashMap<>(); }

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {	return Inventory.instance; }
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for(BookInventoryInfo book: inventory)
			bookInventory.put(book.getBookTitle(), book);
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		synchronized (bookInventory.get(book)){
			BookInventoryInfo temp = bookInventory.get(book);
			if(temp.getAmountInInventory()>0) {
				temp.take();
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
			else
				return OrderResult.NOT_IN_STOCK;
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		synchronized (bookInventory.get(book)){
			BookInventoryInfo temp = bookInventory.get(book);
			if(temp.getAmountInInventory()>0)
				return temp.getPrice();
			else
				return -1;
		}
	}
	
	/**
     *
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) {
		HashMap<String, Integer> hMap = new HashMap<>();
		bookInventory.forEach((name, book) -> {
			int amount=book.getAmountInInventory();
			if(amount==-1)
				amount=0;
			hMap.put(name, amount); } );
		try {
			FileOutputStream fileOut = new FileOutputStream((filename));
			ObjectOutputStream oos = new ObjectOutputStream(fileOut);
			oos.writeObject(hMap);
			oos.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
