package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.controllers.Controller;

/**
 * ControllerNumerator is a backend independent
 * manager of controller identification (numeration),
 * either given by the backend (i.e. OuyaControllerNumerator)
 * or "comes-first-is-first" (DefaultControllerNumerator).
 */
public abstract class ControllerNumerator {

	public ControllerNumerator() {
	}

	public int getPlayerForController(Controller controller) {
		Controller[] controllers = getControllers();
		for (int i = 0; i < controllers.length; i++) {
			if (controller == controllers[i]) {
				return i;
			}
		}
		return -1;
	}

	public Controller getControllerForPlayer(int player) {
		Controller[] controllers = getControllers();
		if (controllers == null) {
			return null;
		}
		if (player < 0 || player >= controllers.length) {
			return null;
		}
		return controllers[player];
	}

	public int getNextPlayer(int start) {
		Controller[] controllers = getControllers();
		if (controllers == null) {
			return -1;
		}
		if (start < 0 || start >= controllers.length) {
			return -1;
		}
		for (int i = start; i < controllers.length; i++) {
			if (controllers[i] != null) {
				return i;
			}
		}
		return -1;
	}

	public int getPrevPlayer(int start) {
		Controller[] controllers = getControllers();
		if (controllers == null) {
			return -1;
		}
		if (start < 0 || start >= controllers.length) {
			return -1;
		}
		for (int i = start; i >= 0; i--) {
			if (controllers[i] != null) {
				return i;
			}
		}
		return -1;
	}

	public Controller getNextController(int start) {
		Controller[] controllers = getControllers();
		if (controllers == null) {
			return null;
		}
		if (start < 0 || start >= controllers.length) {
			return null;
		}
		for (int i = start; i < controllers.length; i++) {
			if (controllers[i] != null) {
				return controllers[i];
			}
		}
		return null;
	}

	public Controller getPrevController(int start) {
		Controller[] controllers = getControllers();
		if (controllers == null) {
			return null;
		}
		if (start < 0 || start >= controllers.length) {
			return null;
		}
		for (int i = start; i >= 0; i--) {
			if (controllers[i] != null) {
				return controllers[i];
			}
		}
		return null;
	}

	public abstract Controller[] getControllers();
}
