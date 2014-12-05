package net.fourbytes.shadow.network;

/**
 * Sent to everyone when someone left the game.
 */
public class DataDisconnected extends DataChatMessage {

	public String nickname;
	public String message;

	public DataDisconnected() {
		this(null, null);
	}

	public DataDisconnected(String nickname, String message) {
        super(message, null, null, DataChatMessage.SYSTEM);
		this.nickname = nickname;
		this.message = message;
	}

}
