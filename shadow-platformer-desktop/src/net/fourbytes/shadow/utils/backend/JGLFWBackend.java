package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;

public class JGLFWBackend extends DesktopBackend {

	public JglfwApplicationConfiguration cfgApp;

	public JGLFWBackend(JglfwApplicationConfiguration cfgApp) {
		super();
		this.cfgApp = cfgApp;
	}

}
