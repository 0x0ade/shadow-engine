package net.fourbytes.shadow.network;

/**
 * Object sent through / with NetStreams should extend this class.
 */
public abstract class Data {

	protected boolean ordered = true;

	public Data() {
	}

	/**
	 * Returns whether this data instance should be sent ordered (TCP) or unordered (UDP).
	 * @param ordered new value to be get via {@link #getOrdered()}
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	/**
	 * Returns whether this data instance should be sent ordered (TCP) or unordered (UDP).
	 * @return true if to be sent via TCP; false otherwise
	 */
	public boolean getOrdered() {
		return ordered;
	}

}
