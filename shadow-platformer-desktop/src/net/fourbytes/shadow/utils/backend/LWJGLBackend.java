package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LWJGLBackend extends Backend {

	public LwjglApplicationConfiguration cfgApp;

	public LWJGLBackend(LwjglApplicationConfiguration cfgApp) {
		super();
		this.cfgApp = cfgApp;
	}

	@Override
	public void create() {
	}

	@Override
	public ModLoader newModLoader() {
		return new DesktopModLoader();
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new DefaultControllerNumerator();
	}
}
