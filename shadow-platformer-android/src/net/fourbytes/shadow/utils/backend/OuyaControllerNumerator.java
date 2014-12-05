package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.controllers.Controller;
import net.fourbytes.slimodk.SlimODKController;

/**
 * This class requires the SlimODKController as it's operations are
 * done by the ODK by default. The use of this class also requires
 * an initialized SlimODK and initialized SlimODKController class.
 */
public class OuyaControllerNumerator extends ControllerNumerator {

	@Override
	public Controller[] getControllers() {
		return SlimODKController.controllers;
	}

}
