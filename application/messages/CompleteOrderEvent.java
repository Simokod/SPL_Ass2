package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.services.SellingService;

public class CompleteOrderEvent implements Event {
    private SellingService seller;

    public CompleteOrderEvent(SellingService seller){ this.seller=seller; }

    public SellingService getSeller() {
        return seller;
    }
}
