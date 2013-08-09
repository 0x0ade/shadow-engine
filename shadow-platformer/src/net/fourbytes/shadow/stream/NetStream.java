package net.fourbytes.shadow.stream;

/**
 * This class is the standard networking class implementing {@link IStream} to support querying {@link Data} to send and 
 * to link to the {@link IStreamNet#update} method when {@link #tick()}ing. <br> 
 * All network servers and classes should extend this class. 
 */
public abstract class NetStream implements IStream, IStreamNet {
	
	public NetStream() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void tick() {
		//TODO Send objects in queue
		//TODO create queue first
		
		update();
	}
	
}
