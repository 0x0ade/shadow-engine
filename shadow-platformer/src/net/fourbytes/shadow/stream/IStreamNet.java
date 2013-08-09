package net.fourbytes.shadow.stream;

/**
 * This interface contains base methods / fields used for networking. 
 * It's extend by {@IStreamClient} and {@IStreamServer}.
 */
public interface IStreamNet {
	
	/**
	 * update() is client / server dependant as it forwards the update call to the underlying implementation.
	 */
	public void update();
	
}
