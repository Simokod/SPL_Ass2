package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
        // Sent by SellingService, received by InventoryService
public class TakeFromInventoryEvent implements Event<Boolean> {

    private String bookTitle;

    public TakeFromInventoryEvent(String bookTitle){ this.bookTitle = bookTitle; }

    public String getBookTitle() {
        return bookTitle;
    }

}
