package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {

	private final String bookTitle;
	private int amountInInventory;
	private final int price;

	BookInventoryInfo(String bookTitle, int amountInInventory, int price){
		this.bookTitle=bookTitle;
		this.amountInInventory=amountInInventory;
		this.price=price;
	}
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amountInInventory;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}

	/**
	 * Reduces the amount of this book in the store's inventory by 1
	 */
	public void take(){
			amountInInventory--;
	}

	/**
	 * @return a string describing the book
	 */
	public String toString(){
		return "Book name: "+bookTitle+", Amount in inventory: "+amountInInventory+", Price: "+price;
	}
	
}
