package net.fourbytes.shadow.network;

import net.fourbytes.shadow.Shadow;

/**
 * Defines a chat message (public, private, system, ...).
 */
public class DataChatMessage extends Data {

    public static int SYSTEM = 1;

    public String message;
    public String sender;
    public String recipient;
    public int flags;

    public DataChatMessage() {
        this("Hello, World!", Shadow.playerInfo.getUserName(), null, 0);
    }

    public DataChatMessage(String message) {
        this(message, Shadow.playerInfo.getUserName(), null, 0);
    }

    public DataChatMessage(String message, String recipient) {
        this(message, Shadow.playerInfo.getUserName(), recipient, 0);
    }

    public DataChatMessage(String message, String sender, String recipient, int flags) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
        this.flags = flags;
    }

}
