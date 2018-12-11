package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class CompleteOrderEvent implements Event {


    private OrderReceipt receipt;

    CompleteOrderEvent(OrderReceipt receipt){
        this.receipt = receipt;
    }
    public OrderReceipt getReceipt() {
        return receipt;
    }
}
