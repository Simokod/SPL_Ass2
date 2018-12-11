package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BookOrderEvent implements Event {

    private int price;

    public BookOrderEvent(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

}
