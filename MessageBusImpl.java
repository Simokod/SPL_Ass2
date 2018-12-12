package bgu.spl.mics;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl instance = new MessageBusImpl();
	private HashMap<MicroService, LinkedBlockingQueue<Message>> msgQueues;						// a message queue for each ms
	private HashMap<Class<? extends Event> , ConcurrentLinkedQueue<MicroService>> eventSubs;	// saving subs to events
	private HashMap<Class<? extends Broadcast> , LinkedList<MicroService>> brdSubs;				// saving subs to broadcasts
	private HashMap<Event, Future> eventFutures;

	// Private Constructor
	private MessageBusImpl(){
		msgQueues = new HashMap<>();
		eventSubs = new HashMap<>();
		brdSubs = new HashMap<>();
		eventFutures = new HashMap<>();
	}

	public static MessageBusImpl getInstance() { return MessageBusImpl.instance; }

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
		while(eventFutures.get(e)==null);			// TODO: fix this
		eventFutures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for(MicroService m: brdSubs.get(b.getClass()))
			msgQueues.get(m).offer(b);
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class evCls = e.getClass();
		if(eventSubs.get(evCls) ==null) {
			System.out.println("no such queue");
			return null;
		}
		msgQueues.get(eventSubs.get(evCls).peek()).offer(e);		// Adding the event to the microService in line
		eventSubs.get(evCls).offer(eventSubs.get(evCls).poll());	// Moving the microService to the back of the queue according to round robin
		Future<T> temp = new Future<>();							// Saving the future in order to resolve it in the future
		eventFutures.put(e, temp);
		return temp;
	}

	@Override
	public void register(MicroService m) {
		msgQueues.put(m, new LinkedBlockingQueue<>());
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
