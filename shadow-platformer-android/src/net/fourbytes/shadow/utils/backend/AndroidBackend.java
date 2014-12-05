package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidInput;
import net.fourbytes.shadow.Camera;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.utils.PlayerInfo;

public class AndroidBackend extends Backend {

	public AndroidApplicationConfiguration cfgApp;

	public AndroidBackend(AndroidApplicationConfiguration cfgApp) {
		super();
		this.cfgApp = cfgApp;
	}

	@Override
	public void create() {
		GameObject.pixffac = 2;
		Camera.blursize = 4f;
	}

	@Override
	public void dispose() {
	}

	@Override
	public ModLoader newModLoader() {
		return new AndroidModLoader();
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new DefaultControllerNumerator();
	}

	@Override
	public PlayerInfo newPlayerInfo() {
		//TODO load the info from somewhere
		String userName = "";
		String userID = "";
		return new PlayerInfo(userName, userID, "");
	}

	public AndroidInput getGdxInput() {
		return null;
	}
}
