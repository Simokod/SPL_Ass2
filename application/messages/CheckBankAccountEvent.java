package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckBankAccountEvent implements Event {
    public int price;

    CheckBankAccountEvent(int price){
        this.price=price;
    }
}
