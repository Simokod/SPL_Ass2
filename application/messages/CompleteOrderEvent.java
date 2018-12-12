package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class CompleteOrderEvent implements Event<OrderReceipt> {
    private String seller;
    private BookInventoryInfo book;

    public CompleteOrderEvent(String seller, BookInventoryInfo book){
        this.seller=seller;
        this.book=book;
    }

    public String getSeller() {
        return this.seller;
    }
    public BookInventoryInfo getBook() {
        return this.book;
    }
}
