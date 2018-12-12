package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

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

	public ResourceService() {
		super("Change_This_Name");
		resources = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		Callback<AcquireVehicleEvent> tryAcquireVehicle = (e) -> {
			Future<DeliveryVehicle> futureVehicle = resources.acquireVehicle();
			complete(e, futureVehicle.get());
		};
		subscribeEvent(AcquireVehicleEvent.class, tryAcquireVehicle);

		Callback<ReleaseVehicleEvent> releaseVehicle = (e) -> {
			resources.releaseVehicle(e.getVehicle());
		};
		subscribeEvent(ReleaseVehicleEvent.class, releaseVehicle);
	}

}
