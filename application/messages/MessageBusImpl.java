package bgu.spl.mics;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl instance = new MessageBusImpl();
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> msgQueues;					// a message queue for each MicroService
	private ConcurrentHashMap<Class<? extends Event> , ConcurrentLinkedQueue<MicroService>> eventSubs;	// a queue of MicroServices for each type of Event
	private ConcurrentHashMap<Class<? extends Broadcast> , LinkedList<MicroService>> brdSubs;			// a queue of MicroServices for each type of BroadCast
	private ConcurrentHashMap<Event, Future> eventFutures;												// matching Events and their Futures

	// Private Constructor
	private MessageBusImpl(){
		msgQueues = new ConcurrentHashMap<>();
		eventSubs = new ConcurrentHashMap<>();
		brdSubs = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() { return MessageBusImpl.instance; }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventSubs) {
			eventSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (brdSubs) {
			brdSubs.computeIfAbsent(type, k -> new LinkedList<>()).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		synchronized (eventFutures) {
			while(eventFutures.get(e)==null)
				try {
					eventFutures.wait();
				} catch (InterruptedException e1) {
					return;
				}
		}
		eventFutures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(brdSubs.get(b.getClass())==null || brdSubs.get(b.getClass()).isEmpty()) {
			return;
		}
		synchronized (brdSubs) {
			for(MicroService m: brdSubs.get(b.getClass())) {
				msgQueues.get(m).offer(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class eventClass = e.getClass();
		synchronized (eventSubs) {
													// in-case the micro services, who are supposed to be subscribed
			if(eventSubs.get(eventClass)==null)		// to this type of event, terminated, send a future holding null
				return null;
			if (eventSubs.get(eventClass).isEmpty()) {
				Future<T> f = new Future<>();
				f.resolve(null);
				return f;
			}
 			msgQueues.get(eventSubs.get(eventClass).peek()).offer(e);			// Adding the event to the microService in line
			eventSubs.get(eventClass).offer(eventSubs.get(eventClass).poll());	// Moving the microService to the back of the queue according to round robin
		}
		Future<T> temp = new Future<>();
		synchronized (eventFutures) {
			eventFutures.put(e, temp);
			eventFutures.notifyAll();
		}
		return temp;
	}

	@Override
	public void register(MicroService m) {
		synchronized (msgQueues) {
			msgQueues.put(m, new LinkedBlockingQueue<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (eventSubs) {			// removing this microservice m from the subscribing lists
			eventSubs.forEach((k,v) -> v.remove(m));
		}
		synchronized (brdSubs) {
			brdSubs.forEach((k,v) -> v.remove(m));
		}									// removing this microservice's message queue from the messageBus
		synchronized (msgQueues) {
			msgQueues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return msgQueues.get(m).take();
	}
}
