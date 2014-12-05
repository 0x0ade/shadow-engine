package net.fourbytes.shadow.utils.backend;

import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.ModManager;

public final class BackendHelper {
	private BackendHelper() {
	}

	public static void checkBackend() {
		if (backend == null) {
			throw new IllegalStateException("No Shadow backend found!");
		}
	}

	public static Backend backend;
	public static void setUp() {
		checkBackend();

		backend.create();

		ModManager.loader = backend.newModLoader();
		Shadow.controllerHelper.numerator = backend.newControllerNumerator();
	}

}
