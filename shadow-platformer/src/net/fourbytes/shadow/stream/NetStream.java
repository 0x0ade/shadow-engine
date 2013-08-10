package net.fourbytes.shadow.stream;

/**
 * This class is the standard networking class implementing {@link IStream} to support queuing {@link Data} to send and 
 * to link to the {@link #update()} method when {@link #tick()}ing. <br> 
 * All network servers and classes should extend this class. 
 */
public abstract class NetStream implements IStream {
	
	public NetStream() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void tick() {
		//TODO Send objects in queue
		//TODO create queue first
		
		update();
	}
	
	/**
	 * Queues the given object or sends directly when possible.
	 * @param o Object to send
	 */
	public final void send(Object o) {
		//TODO Add object to queue
		
	}
	
	/**
	 * Called internally when sending object. Subclasses should override this instead of {@link #send()}.
	 */
	public abstract void send0(Object o);
	
	/**
	 * update() is client / server dependant as it forwards the update call to the underlying implementation.
	 */
	public abstract void update();
	
}
