package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
            // sent by LogisticsService, handled by ResourceService
public class ReleaseVehicleEvent implements Event<Boolean> {

    private DeliveryVehicle vehicle;

    public ReleaseVehicleEvent(DeliveryVehicle vehicle) { this.vehicle = vehicle; }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}
