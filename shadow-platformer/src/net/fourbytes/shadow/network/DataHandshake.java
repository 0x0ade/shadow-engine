package net.fourbytes.shadow.network;

/**
 * Initial data sent first from client to server, then server to client.
 */
public abstract class DataHandshake {
	
	public String nick;
	public String clientID;
	
	public DataHandshake() {
	}
	
}
