package bgu.spl.mics;

import bgu.spl.mics.application.messages.TimeTick;

import java.util.Iterator;
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
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> msgQueues;					// a message queue for each ms
	private ConcurrentHashMap<Class<? extends Event> , ConcurrentLinkedQueue<MicroService>> eventSubs;	// saving subs to events
	private ConcurrentHashMap<Class<? extends Broadcast> , LinkedList<MicroService>> brdSubs;			// saving subs to broadcasts
	private ConcurrentHashMap<Event, Future> eventFutures;

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
					System.out.println("interrupted while waiting for future");		// TODO: remove syso
				}
		}
		eventFutures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(brdSubs.get(b.getClass())==null || brdSubs.get(b.getClass()).isEmpty()) {
			System.out.println("no subs for this broadcast");                        //TODO: remove sout
			return;
		}
		synchronized (brdSubs) {
			for(MicroService m: brdSubs.get(b.getClass())){					// TODO: check why null error
				System.out.println(msgQueues.get(m));						// TODO remove sout
				msgQueues.get(m).offer(b);
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class eventClass = e.getClass();
		synchronized (eventSubs) {
			if(eventSubs.get(eventClass)==null)
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
		synchronized (eventSubs) {
			eventSubs.forEach((k,v) -> v.remove(m));
		}
		synchronized (brdSubs) {
			brdSubs.forEach((k,v) -> v.remove(m));
		}
		synchronized (msgQueues) {
			msgQueues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return msgQueues.get(m).take();
	}
}
