package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;

public class CheckAvailabilityEvent implements Event<Integer> {
    private BookInventoryInfo name;

    public CheckAvailabilityEvent(BookInventoryInfo name){ this.name=name; }

    public BookInventoryInfo getName() { return this.name; }
}
