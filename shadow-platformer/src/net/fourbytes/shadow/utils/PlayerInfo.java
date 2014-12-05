package net.fourbytes.shadow.utils;

public class PlayerInfo {

	protected String userName;
	protected String userID;
	protected String sessionID;

	public PlayerInfo() {
		this(Options.getString("mp.user.name"), Options.getString("mp.user.id"), Options.getString("mp.user.session"));
	}

	public PlayerInfo(String userName, String userID, String sessionID) {
		this.userName = userName;
		this.userID = userID;
		this.sessionID = sessionID;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserID() {
		return userID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
