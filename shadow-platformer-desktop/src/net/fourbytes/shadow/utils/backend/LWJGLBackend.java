package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LWJGLBackend extends DesktopBackend {

	public LwjglApplicationConfiguration cfgApp;

	public LWJGLBackend(LwjglApplicationConfiguration cfgApp) {
		super();
		this.cfgApp = cfgApp;
	}

}
