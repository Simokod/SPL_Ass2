package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class getBookInventoryInfoEvent implements Event<BookInventoryInfo> {

    private String book;

    public getBookInventoryInfoEvent(String book){
        this.book = book;
    }
    public String getBook() {
        return book;
    }

}
