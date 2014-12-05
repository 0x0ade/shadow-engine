package net.fourbytes.shadow.network;

/**
 * Sent to someone when being kicked.
 */
public class DataKick extends Data {

	public String message;

	public DataKick() {
		this(null);
	}

	public DataKick(String message) {
		this.message = message;
	}

}
