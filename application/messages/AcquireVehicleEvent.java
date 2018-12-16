package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
            // sent by LogiticsService, handled by ResourceService
public class AcquireVehicleEvent implements Event<Future<DeliveryVehicle>> {
}
