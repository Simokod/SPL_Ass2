package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class BookOrderEvent implements Event<Boolean>{

    private BookInventoryInfo book;

    public BookOrderEvent(BookInventoryInfo book) {
        this.book=book;
    }

    public BookInventoryInfo getBook() { return book; }


}
