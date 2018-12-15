package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private boolean isInitialized;

	public LogisticsService(String name) {
		super(name);
		isInitialized = false;
	}

	@Override
	protected void initialize() {
		// terminate this when received
		subscribeBroadcast(TerminateAllBroadcast.class, t -> terminate());

		// Handling delivery event
		Callback<DeliveryEvent> makeDelivery = (deliveryEvent) -> {
			Future<DeliveryVehicle> futureVehicle = sendEvent(new AcquireVehicleEvent());
			DeliveryVehicle vehicle = futureVehicle.get();
			vehicle.deliver(deliveryEvent.getAddress(), deliveryEvent.getDistance());
			sendEvent(new ReleaseVehicleEvent(vehicle));
		};
		subscribeEvent(DeliveryEvent.class, makeDelivery);
		// signaling that the Micro Service has initialized
		isInitialized = true;
	}
	public boolean isInitialized() { return isInitialized; }
}