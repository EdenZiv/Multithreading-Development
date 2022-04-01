package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap <Class<? extends Event<?>>, Queue<MicroService>> eventQueueHashMap; //for each Event hold queue of microservices which is subscribed to this event type
	private HashMap<Class<? extends Broadcast>,Queue<MicroService>> broadcastQueueHashMap;  //for each Broadcast hold queue of microservices which is subscribed to this broadcast
	private HashMap <MicroService, BlockingQueue<Message>> microServiceHashMap; //for each microservice hold queue of messages needed to be executed
	private ConcurrentHashMap <Event<?>,Future<?>> eventFutureHashMap;
	private final Object lockSubscribeEvent;
	private final Object lockSubscribeBroadcast;

	private static class messageBusSingeltonHolder{  //singelton implementation
		private static final MessageBusImpl instance= new MessageBusImpl();
	}

	private MessageBusImpl(){  //constructor
		eventQueueHashMap = new HashMap<>();
		broadcastQueueHashMap = new HashMap<>();
		microServiceHashMap= new HashMap<>();
		eventFutureHashMap=new ConcurrentHashMap<>();
		lockSubscribeEvent=new Object();
		lockSubscribeBroadcast=new Object();
	}

	public static MessageBusImpl getInstance(){  //instance of the singelton object
		return messageBusSingeltonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (microServiceHashMap.containsKey(m)) {
			if (eventQueueHashMap.get(type) == null) {
				synchronized (lockSubscribeEvent) {
					if(eventQueueHashMap.get(type) == null) {
						Queue<MicroService> msq = new ConcurrentLinkedQueue<>();
						msq.add(m);
						eventQueueHashMap.put(type, msq);
					}
				}
			}
			else
				eventQueueHashMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (microServiceHashMap.containsKey(m)) {
			if (broadcastQueueHashMap.get(type) == null) {
				synchronized (lockSubscribeBroadcast) {
					if(broadcastQueueHashMap.get(type) == null) {
						Queue<MicroService> msq = new ConcurrentLinkedQueue<>();
						msq.add(m);
						broadcastQueueHashMap.put(type, msq);
					}
				}
			}
			else
				broadcastQueueHashMap.get(type).add(m);
		}
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		((Future<T>) eventFutureHashMap.get(e)).resolve(result);  // casting - canceling the ? in initialization
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (b!=null && broadcastQueueHashMap.containsKey(b.getClass())) {
			synchronized (broadcastQueueHashMap.get(b.getClass())) {
				for (MicroService m : broadcastQueueHashMap.get(b.getClass())) {
					microServiceHashMap.get(m).add(b);
				}
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (e!=null && eventQueueHashMap.get(e.getClass())!=null) {
			MicroService m;
			synchronized (eventQueueHashMap.get(e.getClass())) {
				m = eventQueueHashMap.get(e.getClass()).poll();
				if(m==null)
					return null;
				eventQueueHashMap.get(e.getClass()).add(m);
			}
			Future <T> future = new Future<>();
			eventFutureHashMap.put(e, future); //first update a related future in the hash map before inserting the event to the microservice queue
			microServiceHashMap.get(m).add(e);
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!microServiceHashMap.containsKey(m)) {
			microServiceHashMap.put(m, new LinkedBlockingQueue<>());
		}

	}

	@Override
	public void unregister(MicroService m) { //foreach lambda nir
		eventQueueHashMap.forEach((event, queue)-> queue.remove(m));
		broadcastQueueHashMap.forEach((broadcast, queue)-> queue.remove(m));
		for (Message message:microServiceHashMap.get(m)) {  //remove any message of the microservice queue from the future hashmap
			if (message instanceof Event<?>)
				complete((Event<?>)message,null);
		}
		microServiceHashMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return microServiceHashMap.get(m).take();
	}
}