package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resources;
	private CountDownLatch latch;

	public ResourceService(String name, CountDownLatch latch) {
		super(name);
		resources = ResourcesHolder.getInstance();
		this.latch = latch;
	}

	@Override
	protected void initialize() {
		// terminate this when received
		subscribeBroadcast(TerminateAllBroadcast.class, t -> terminate());

		// Trying to acquire a vehicle
		Callback<AcquireVehicleEvent> tryAcquireVehicle = (e) -> {
			Future<DeliveryVehicle> futureVehicle = resources.acquireVehicle();
			complete(e, futureVehicle);
		};
		subscribeEvent(AcquireVehicleEvent.class, tryAcquireVehicle);

		// Releasing a vehicle after delivery is complete
		Callback<ReleaseVehicleEvent> releaseVehicle = (e) -> resources.releaseVehicle(e.getVehicle());
		subscribeEvent(ReleaseVehicleEvent.class, releaseVehicle);

		// signaling that the Micro Service has initialized
		latch.countDown();
	}
}