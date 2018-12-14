package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
        // sent by SellingService, handled by APIService
public class CompleteOrderEvent implements Event<OrderReceipt> {

    private String seller;
    private String book;
    private int price;
    private int orderTick;
    private int processTick;

    public CompleteOrderEvent(String seller, String book, int price, int orderTick, int processTick){
        this.seller = seller;
        this.book = book;
        this.price = price;
        this.processTick = processTick;
        this.orderTick = orderTick;
    }

    public String getSeller() {
        return this.seller;
    }
    public String getBook() {
        return this.book;
    }
    public int getPrice() { return price; }
    public int getProcessTick() { return processTick; }
    public int getOrderTick() { return orderTick; }
}
