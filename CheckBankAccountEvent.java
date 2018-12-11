package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckBankAccountEvent implements Event {

    private int price;

    public CheckBankAccountEvent(int price){
        this.price=price;
    }

    public int getPrice() {
        return price;
    }

}

