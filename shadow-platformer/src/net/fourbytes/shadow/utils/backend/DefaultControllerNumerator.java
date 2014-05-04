package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.controllers.Controller;
import net.fourbytes.shadow.Shadow;

/**
 * Default ControllerNumerator used by backends not offering / made
 * for numbering of currently used controllers. It's not as
 * "fast" as other ControllerNumerators as others handle events of
 * connection and disconnection and this numerator polls for connected
 * controllers every call to getControllers as ControllerNumerators
 * should not listen to GDX's Controllers class but rather to
 * Shadow's ControllerHelper instance.
 */
public class DefaultControllerNumerator extends ControllerNumerator {

	protected Controller[] controllers;
	protected int maximum;

	public DefaultControllerNumerator() {
		this(0);
	}

	public DefaultControllerNumerator(int maximum) {
		this.maximum = maximum;
		if (maximum > 0) {
			controllers = new Controller[maximum];
		}
	}

	@Override
	public Controller[] getControllers() {
		if (maximum < 1 &&
				(controllers == null || controllers.length != Shadow.controllerHelper.controllers.size)) {
			controllers = Shadow.controllerHelper.controllers.toArray(Controller.class);
		} else {
			for (int i = 0; i < controllers.length; i++) {
				if (i < Shadow.controllerHelper.controllers.size) {
					controllers[i] = Shadow.controllerHelper.controllers.get(i);
				} else {
					controllers[i] = null;
				}
			}
		}
		return controllers;
	}

}
