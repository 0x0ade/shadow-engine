package net.fourbytes.shadow.network;

import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.utils.PlayerInfo;

/**
 * Initial data sent first from client to server, then server to client.
 */
public class DataHandshake extends Data {

    public String gameID;
	public String clientName;
	public String clientUUID;
	public String clientSessionID;
	
	public DataHandshake() {
		this(null, null, null);
	}

	public DataHandshake(PlayerInfo playerInfo) {
		this(playerInfo.getUserName(), playerInfo.getUserID(), playerInfo.getSessionID());
	}

	public DataHandshake(String clientName, String clientUUID, String clientSessionID) {
        this.gameID = Shadow.gameID;
		this.clientName = clientName;
		this.clientUUID = clientUUID;
		this.clientSessionID = clientSessionID;
	}

}
