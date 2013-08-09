package net.fourbytes.shadow.stream;

/**
 * An IStream is an general interface for streams sending {@link Data} (or similar) to 
 * other end points (network) or the file system (save file).
 */
public interface IStream {
	
	/**
	 * This method sends {@link Data} in queue (if any) and 
	 * updates the underlying client / server implementation (if any).
	 */
	public void tick();
	
}
