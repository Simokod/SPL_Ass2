package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
         // sent by APIService, handled by SellingService
public class BookOrderEvent implements Event<Boolean>{

    private String book;
    private Customer c;
    private int orderTick;

    public BookOrderEvent(String book, Customer c, int orderTick) {
        this.book = book;
        this.c = c;
        this.orderTick = orderTick;
    }

    public String getBook() { return book; }
    public Customer getCustomer() { return c; }
    public int getOrderTick() { return orderTick; }


}
