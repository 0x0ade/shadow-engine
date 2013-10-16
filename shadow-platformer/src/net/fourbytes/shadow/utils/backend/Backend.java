package net.fourbytes.shadow.utils.backend;

import net.fourbytes.shadow.utils.backend.opengl.GLUtil;

public abstract class Backend {

	public abstract void create();

	public GLUtil getGLUtil() {
		return new GLUtil();
	}

}
