package net.fourbytes.shadow.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import net.fourbytes.shadow.utils.Cache;

import java.io.IOException;

/**
 * This class is the standard networking class supporting queuing {@link Data} to send and 
 * to handle received data when {@link #tick()}ing. <br> 
 */
public abstract class NetStream {

	protected Cache<Entry> entries = new Cache<Entry>(Entry.class, 1024);
	
	public static int portTCP = 1337; //TODO Find better port.
    public static int portUDP = 1338; //TODO Find better port.
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
            if (e == null) {
                continue;
            }
			send(e.value, e.key, true);
            queueSend.removeValue(e, true);
			i++;
		}

		for (Entry e : queueHandle) {
            if (e == null) {
                continue;
            }
			handle((Data) e.value, e.key);
            queueHandle.removeValue(e, true);
		}
	}
	
	/**
	 * Queues the given object to send later on.
	 * @param o Object to send
	 */
	public final void send(Object o) {
		send(o, null, false);
	}
	
	/**
	 * Queues the given object or sends directly when needed.
	 * @param o Object to send
	 * @param target Target, for example an Connection in KryoNet
	 * @param priority true whether to send now, false if the operation should be queued.
	 */
	public final void send(Object o, Object target, boolean priority) {
		if (priority) {
			Data data;

			if (o instanceof Data) {
				data = (Data) o;
			} else {
				data = new DataObject(o);
			}

			if (data.getOrdered()) {
				sendTCP(data, target);
			} else {
				sendUDP(data, target);
			}
		} else {
			Entry entry = new Entry();
			entry.key = target;
			entry.value = o;
			queueSend.add(entry);
		}
	}

	/**
	 * Called internally when sending data. Subclasses should override this instead of {@link #send(Object)}.
	 */
	protected abstract void sendTCP(Data data, Object target);

	/**
	 * Called internally when sending data. Subclasses should override this instead of {@link #send(Object)}.
	 */
	protected abstract void sendUDP(Data data, Object target);

	/**
	 * This method forwards the data got to the sever / client to be handled.
	 */
	public abstract void handle(Data data, Object target);

    /**
     * Starts the server. Doesn't do anything on clients.
     */
    public abstract void start();

    /**
     * Connects to the given IP. Doesn't do anything on servers.
     */
    public abstract void connect(String ip);

    /**
     * Disconnects from the currently connected server or kills the connections to all clients.
     */
    public abstract void disconnect();

}
