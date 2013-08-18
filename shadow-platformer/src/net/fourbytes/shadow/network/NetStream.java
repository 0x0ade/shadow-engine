package net.fourbytes.shadow.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * This class is the standard networking class supporting queuing {@link Data} to send and 
 * to handle received data when {@link #tick()}ing. <br> 
 * It's using KryoNet as underlying implementation.
 */
public abstract class NetStream {
	
	public static int port = 1157; //TODO Find better port.
	public static int bufferObject = 4096;
	public static int bufferWriteClient = bufferObject*5;
	public static int bufferWriteServer = bufferObject*10;
	public static int maxSent = 10; //TODO Find perfect count
	
	public Array<Entry> queueSend = new Array<Entry>();
	public Array<Entry> queueHandle = new Array<Entry>();
	
	public NetStream() {
	}
	
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
	 * Registers the EndPoint (Server, Client). Should be called after creating it.
	 * @param ep EndPoint / Server / Client / ... to register
	 */
	public void register(EndPoint ep) {
		Kryo kryo = ep.getKryo();
		kryo.setRegistrationRequired(false);
	}
}
