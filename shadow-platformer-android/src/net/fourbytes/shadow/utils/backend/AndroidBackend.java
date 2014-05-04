package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidInput;

public class AndroidBackend extends Backend {

	public AndroidApplicationConfiguration cfgApp;

	public AndroidBackend(AndroidApplicationConfiguration cfgApp) {
		super();
		this.cfgApp = cfgApp;
	}

	@Override
	public void create() {

	}

	@Override
	public ModLoader newModLoader() {
		return new AndroidModLoader();
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new DefaultControllerNumerator();
	}

	public AndroidInput getGdxInput() {
		return null;
	}
}
