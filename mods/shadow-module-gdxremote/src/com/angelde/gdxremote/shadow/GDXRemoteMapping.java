package com.angelde.gdxremote.shadow;

import com.badlogic.gdx.controllers.Controller;
import net.fourbytes.shadow.ControllerHelper;
import net.fourbytes.shadow.Input;

public final class GDXRemoteMapping {
	private GDXRemoteMapping() {
	}

	public final static int A_X = 0;
	public final static int A_Y = 1;

	public final static int B_UP = 0;
	public final static int B_DOWN = 1;
	public final static int B_LEFT = 2;
	public final static int B_RIGHT = 3;
	public final static int B_JUMP = 0;
	public final static int B_PAUSE = 4;
	public final static int B_ACCEPT = 5;
	public final static int B_BACK = 6;

	public static void map(ControllerHelper controllerHelper, Controller controller) {
		System.out.println("Automapping GDXRemote controller...");
		//TODO Update bindings
		controllerHelper.map(Input.up, new ControllerHelper.ControllerButton(controller, B_UP));
		controllerHelper.map(Input.down, new ControllerHelper.ControllerButton(controller, B_DOWN));
		controllerHelper.map(Input.left, new ControllerHelper.ControllerButton(controller, B_LEFT));
		controllerHelper.map(Input.right, new ControllerHelper.ControllerButton(controller, B_RIGHT));
		controllerHelper.map(Input.up, new ControllerHelper.ControllerAxis(controller, A_Y, true));
		controllerHelper.map(Input.down, new ControllerHelper.ControllerAxis(controller, A_Y, false));
		controllerHelper.map(Input.left, new ControllerHelper.ControllerAxis(controller, A_X, true));
		controllerHelper.map(Input.right, new ControllerHelper.ControllerAxis(controller, A_X, false));
		controllerHelper.map(Input.jump, new ControllerHelper.ControllerButton(controller, B_JUMP));
		controllerHelper.map(Input.pause, new ControllerHelper.ControllerButton(controller, B_PAUSE));
		controllerHelper.map(Input.enter, new ControllerHelper.ControllerButton(controller, B_ACCEPT));
		controllerHelper.map(Input.androidBack, new ControllerHelper.ControllerButton(controller, B_BACK));
	}
}
