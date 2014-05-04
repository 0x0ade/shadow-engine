package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;

public class JGLFWBackend extends Backend {

	public JglfwApplicationConfiguration cfgApp;

	public JGLFWBackend(JglfwApplicationConfiguration cfgApp) {
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
