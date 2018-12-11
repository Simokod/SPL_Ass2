package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class CompleteOrderEvent implements Event {

    public OrderReceipt receipt;

    CompleteOrderEvent(){
        //this.receipt = new OrderReceipt();
    }
}
