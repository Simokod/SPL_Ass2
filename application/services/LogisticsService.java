package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.TimeUnit;

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
	private int maxDeliveryTime;

	public LogisticsService(String name, int maxDeliveryTime) {
		super(name);
		this.isInitialized = false;
		this.maxDeliveryTime = maxDeliveryTime;
	}

	@Override
	protected void initialize() {
		// terminate this when received
		subscribeBroadcast(TerminateAllBroadcast.class, t -> terminate());

		// Handling delivery event
		Callback<DeliveryEvent> makeDelivery = (deliveryEvent) -> {
			Future<Future<DeliveryVehicle>> futureVehicle = sendEvent(new AcquireVehicleEvent());
			// the future or it's value will be null if the program finishes while this service is doing a delivery
			if(futureVehicle == null || futureVehicle.get(maxDeliveryTime, TimeUnit.SECONDS)==null)
				return;
			DeliveryVehicle vehicle = futureVehicle.get().get(maxDeliveryTime, TimeUnit.SECONDS);
			vehicle.deliver(deliveryEvent.getAddress(), deliveryEvent.getDistance());
			sendEvent(new ReleaseVehicleEvent(vehicle));
		};
		subscribeEvent(DeliveryEvent.class, makeDelivery);
		// signaling that the Micro Service has initialized
		isInitialized = true;
	}
	public boolean isInitialized() { return isInitialized; }
}