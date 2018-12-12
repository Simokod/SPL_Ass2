package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class CheckAvailabilityEvent implements Event<Integer> {
    private String name;

    public CheckAvailabilityEvent(String name){this.name=name;}

    public String getName() { return this.name; }
}
