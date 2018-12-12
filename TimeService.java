package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TimeTickBroadcast;
import java.util.Timer;
import java.util.TimerTask;
import bgu.spl.mics.application.passiveObjects.*;
/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TimeTickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private int currentTime;
	private Timer timer;

	public TimeService(int speed, int duration) {
		super("Change_This_Name");
		this.speed = speed;
		this.duration = duration+1;
		this.currentTime = 1;
		this.timer = new Timer();
	}

	@Override
	protected void initialize() {
		TimerTask tick = new TimerTask() {
			@Override
			public void run() {
				if(currentTime<duration) {
					sendBroadcast(new TimeTickBroadcast());
					currentTime++;
				}
				else
					timer.cancel();
			}
		};

		timer.scheduleAtFixedRate(tick, 0, speed);
	}

}
