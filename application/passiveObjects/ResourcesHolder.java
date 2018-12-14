package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static ResourcesHolder instance = new ResourcesHolder();
	private ConcurrentLinkedQueue<DeliveryVehicle> vehiclesQ;

	/**
	 * Constructor
	 */
	private ResourcesHolder() { vehiclesQ = new ConcurrentLinkedQueue<>(); }

	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() { return ResourcesHolder.instance; }
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> futureVehicle = new Future<>();
		synchronized (vehiclesQ) {
			try {
				while(vehiclesQ.isEmpty())
					this.wait();
				futureVehicle.resolve(vehiclesQ.poll());
			} catch (InterruptedException e) {
				System.out.println("interrupted while waiting for a vehicle");
			}
		}
		return futureVehicle;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (vehiclesQ) {
			vehiclesQ.offer(vehicle);
			this.notifyAll();
		}
	}

	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(DeliveryVehicle vehicle: vehicles)
			vehiclesQ.offer(vehicle);
	}

}
