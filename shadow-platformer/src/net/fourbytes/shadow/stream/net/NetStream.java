package net.fourbytes.shadow.stream.net;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import net.fourbytes.shadow.stream.Data;
import net.fourbytes.shadow.stream.IStream;

/**
 * This class is the standard networking class implementing {@link IStream} to support queuing {@link Data} to send and 
 * to link to the {@link #update()} method when {@link #tick()}ing. <br> 
 * All network servers and classes should extend this class. 
 */
public abstract class NetStream implements IStream {
	
	public static int port = 1157; //TODO Find better port.
	public static int bufferObject = 4096;
	public static int bufferWriteClient = bufferObject*5;
	public static int bufferWriteServer = bufferObject*10;
	public static int maxSent = 10; //TODO Find perfect count
	
	public Array<Entry> queueSend = new Array<Entry>();
	public Array<Entry> queueHandle = new Array<Entry>();
	
	public NetStream() {
	}
	
	@Override
	public void tick() {
		int i = 0;
		for (Entry e : queueSend) {
			if (i >= maxSent) {
				break;
			}
			send0(e.value, e.key);
			i++;
		}
		
		for (Entry e : queueHandle) {
			handle(e.value, e.key);
		}
	}
	
	/**
	 * Queues the given object to send later on.
	 * @param o Object to send
	 * @see IStream#send(Object)
	 */
	@Override
	public final void send(Object o) {
		send(o, null, false);
	}
	
	/**
	 * Queues the given object or sends directly when needed.
	 * @param o Object to send
	 * @param target Target, for example an Connection in KryoNet
	 * @param priority true whether to send now, false if the operation should be queued.
	 * @see IStream#send(Object)
	 */
	public final void send(Object o, Object target, boolean priority) {
		if (priority) {
			send0(o, target);
		} else {
			Entry entry = new Entry();
			entry.key = target;
			entry.value = o;
			queueSend.add(entry);
		}
	}
	
	/**
	 * Called internally when sending object. Subclasses should override this instead of {@link #send(Object)}.
	 * @see IStream#send(Object)
	 */
	public abstract void send0(Object o, Object target);
	
	/**
	 * This method forwards the data got to the sever / client to be handled.
	 */
	public abstract void handle(Object obj, Object target);
	
	/**
	 * Does nothing as received objects are queued and afterwards handled in the main thread.
	 * @param o
	 */
	@Override
	public void receive(Object o) {
	}
}
