package net.fourbytes.shadow.network;

/**
 * Sent to everyone when someone joins the game.
 */
public class DataConnected extends DataChatMessage {

	public String nickname;
	public String message;

	public DataConnected() {
		this(null, null);
	}

	public DataConnected(String nickname, String message) {
        super(message, null, null, DataChatMessage.SYSTEM);
		this.nickname = nickname;
		this.message = message;
	}

}
