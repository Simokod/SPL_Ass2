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

	public boolean isSubscribedToTimeTick(MicroService service){
		LinkedList<MicroService> list= brdSubs.get(TimeTick.class);
		for (MicroService m:list) {
			if (m.equals(service))
				return true;
		}
		return false;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubs.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		brdSubs.computeIfAbsent(type, k -> new LinkedList<>()).add(m);
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
		if(brdSubs.get(b.getClass())==null) {
			System.out.println("no subs for this broadcast");                        //TODO: remove sout
			return;
		}
		for(MicroService m: brdSubs.get(b.getClass()))
			msgQueues.get(m).offer(b);
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class eventClass = e.getClass();
		if(eventSubs.get(eventClass)==null) {
			System.out.println("no subs for this event");
			return null;
		}
		msgQueues.get(eventSubs.get(eventClass).peek()).offer(e);			// Adding the event to the microService in line
		eventSubs.get(eventClass).offer(eventSubs.get(eventClass).poll());	// Moving the microService to the back of the queue according to round robin
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
			msgQueues.notifyAll();
		}
	}

	@Override
	public void unregister(MicroService m) {
		LinkedBlockingQueue<Message> temp = new LinkedBlockingQueue<>();
		msgQueues.get(m).drainTo(temp);
		msgQueues.remove(m);
		Iterator it = temp.iterator();
		while (it.hasNext()) {
			if (it.getClass().getName().equals("Broadcast"))
				sendBroadcast((Broadcast) brdSubs.remove(it));
			else
				sendEvent((Event) eventSubs.remove(it));
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return msgQueues.get(m).take();
	}
}
