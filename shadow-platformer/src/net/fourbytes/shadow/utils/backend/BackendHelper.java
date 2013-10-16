package net.fourbytes.shadow.utils.backend;

import net.fourbytes.shadow.utils.backend.opengl.GLUtil;

public final class BackendHelper {

	public static Backend backend;
	public static void setUp() {
		if (backend == null) {
			throw new IllegalStateException("No Shadow backend found!");
		}

		backend.create();

		glUtil = backend.getGLUtil();
	}

	public static GLUtil glUtil;

}
