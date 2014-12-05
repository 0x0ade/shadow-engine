package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import net.fourbytes.shadow.ControllerHelper;
import net.fourbytes.shadow.ControllerHelper.ControllerButton;
import net.fourbytes.shadow.ControllerHelper.ControllerAxis;
import net.fourbytes.shadow.Input;

public class OuyaSystemControllerMapping implements ControllerHelper.Mapping {
	public Array<String> names = new Array<String>(String.class);
	public IntIntMap axes = new IntIntMap();
	public IntIntMap buttons = new IntIntMap();

	@Override
	public String getName() {
		return names.first()+" mapping (OUYA system mapping)";
	}

	@Override
	public boolean map(Controller controller, ControllerHelper controllerHelper) {
		if (!names.contains(controller.getName(), false)) {
			return false;
		}

		System.out.println("Automapping OUYA controller...");
		System.out.println("OUYA controller actually: "+controller.getName());

		controllerHelper.map(Input.up, new ControllerButton(controller, getB(Ouya.BUTTON_DPAD_UP)));
		controllerHelper.map(Input.down, new ControllerButton(controller, getB(Ouya.BUTTON_DPAD_DOWN)));
		controllerHelper.map(Input.left, new ControllerButton(controller, getB(Ouya.BUTTON_DPAD_LEFT)));
		controllerHelper.map(Input.right, new ControllerButton(controller, getB(Ouya.BUTTON_DPAD_RIGHT)));
		controllerHelper.map(Input.up, new ControllerAxis(controller, getA(Ouya.AXIS_LEFT_Y), true));
		controllerHelper.map(Input.down, new ControllerAxis(controller, getA(Ouya.AXIS_LEFT_Y), false));
		controllerHelper.map(Input.left, new ControllerAxis(controller, getA(Ouya.AXIS_LEFT_X), true));
		controllerHelper.map(Input.right, new ControllerAxis(controller, getA(Ouya.AXIS_LEFT_X), false));
		controllerHelper.map(Input.jump, new ControllerButton(controller, getB(Ouya.BUTTON_O)));
		controllerHelper.map(Input.pause, new ControllerButton(controller, getB(Ouya.BUTTON_MENU)));
		controllerHelper.map(Input.enter, new ControllerButton(controller, getB(Ouya.BUTTON_O)));
		controllerHelper.map(Input.androidBack, new ControllerButton(controller, getB(Ouya.BUTTON_A)));

		return true;
	}

	private int getA(int axisOUYA) {
		return axes.get(axisOUYA, axisOUYA);
	}

	private int getB(int buttonOUYA) {
		return buttons.get(buttonOUYA, buttonOUYA);
	}

}
