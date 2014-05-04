package net.fourbytes.shadow;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.Key.Triggerer;
import net.fourbytes.shadow.MenuLevel.MenuItem;
import net.fourbytes.shadow.utils.Cache;
import net.fourbytes.shadow.utils.backend.ControllerNumerator;

/**
 * This class manages all controllers connected to the current
 * application, handles their input and translates them into
 * Shadow key presses. Due to the nature of GDX's Controllers
 * class and it's ControllerManager not managing custom controllers,
 * such as the controllers created by a GDXRemote server, it is
 * advised to use this class to poll for currently connected
 * controllers. Also, this class manages mapping with help of
 * the ControllerInput class and it's subclasses ControllerButton
 * and ControllerAxis. POVs, accelerators and more are to come.
 * <br>
 * This class is instance of ControllerListener but uses polling
 * of Controllers' ControllerManager's controllers. Custom
 * controllers may get added into the list of the Controllers'
 * ControllerManager's controllers but must add the default
 * instance of this class (sitting in Shadow by default) to the
 * list of their ControllerListeners manually and load the
 * mapping manually by calling connected manually.
 */
public final class ControllerHelper implements ControllerListener {

	public static abstract class ControllerInput {
		public Controller controller;

		protected ControllerInput() {
			this(null);
		}

		protected ControllerInput(Controller controller) {
			this.controller = controller;
		}
		
		public abstract String getLabel();
	}
	
	public static class ControllerButton extends ControllerInput {
		public int buttonCode = -1;

		public ControllerButton() {
			super();
		}

		public ControllerButton(ControllerButton input) {
			this(input.controller, input.buttonCode);
		}

		public ControllerButton(Controller controller, int buttonCode) {
			super(controller);
			this.buttonCode = buttonCode;
		}
		
		@Override
		public String getLabel() {
			return "Button "+buttonCode;
		}
		
		@Override
		public int hashCode() {
			return controller.hashCode()*buttonCode;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ControllerButton) {
				ControllerButton button = (ControllerButton) o;
				return button.controller.equals(controller) && button.buttonCode == buttonCode;
			}
			return false;
		}

		public ControllerButton set(Controller controller, int buttonCode) {
			this.controller = controller;
			this.buttonCode = buttonCode;
			return this;
		}
	}
	
	public static class ControllerAxis extends ControllerInput {
		
		public int axisCode = -1;
		public boolean negative = false;

		public ControllerAxis() {
			super();
		}

		public ControllerAxis(ControllerAxis input) {
			this(input.controller, input.axisCode, input.negative);
		}

		public ControllerAxis(Controller controller, int axisCode, boolean negative) {
			super(controller);
			this.axisCode = axisCode;
			this.negative = negative;
		}
		
		@Override
		public String getLabel() {
			return "Axis "+axisCode;
		}
		
		@Override
		public int hashCode() {
			int hash = controller.hashCode()*axisCode;
			if (negative) {
				hash = -hash;
			}
			return hash;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof ControllerAxis) {
				ControllerAxis axis = (ControllerAxis) o;
				return axis.controller.equals(controller) && axis.axisCode == axisCode && axis.negative == negative;
			}
			return false;
		}

		public ControllerAxis set(Controller controller, int axisCode, boolean negative) {
			this.controller = controller;
			this.axisCode = axisCode;
			this.negative = negative;
			return this;
		}
	}

	protected final static Cache<Array<Key>> cacheKeys = new Cache(Array.class, 32,
			new Object[] {false, 4}, new Class[] {boolean.class, int.class});
	protected final static Cache<ControllerButton> cacheButtons = new Cache(ControllerButton.class, 64);
	protected final static Cache<ControllerAxis> cacheAxes = new Cache(ControllerAxis.class, 64);

	public static float deadzone = 0.25f;

	public final ObjectMap<Key, Array<ControllerInput>> mapping = new ObjectMap<Key, Array<ControllerInput>>();
	public final ObjectMap<Key, Array<ControllerInput>> tmpmap = new ObjectMap<Key, Array<ControllerInput>>();
	
	private Key tmpkey;
	public Key assignKey;
	public MenuItem assignKeyHelper;
	public Controller assignKeyController;

	public Array<Controller> controllers = new Array<Controller>();
	public Array<Controller> controllersAuto = new Array<Controller>();

	public ControllerNumerator numerator;

	public ControllerHelper() {
		for (Controller controller : Controllers.getControllers()) {
			connected(controller);
		}
	}

	public void refreshMapping() {
		tmpmap.clear();
		synchronized (mapping) {
			for (Entry<Key, Array<ControllerInput>> entry : mapping.entries()) {
				Array<ControllerInput> inputs = entry.value;
				Key key = entry.key;

				for (ControllerInput input : inputs) {
					if (!controllers.contains(input.controller, true)) {
						inputs.removeValue(input, false);
					}
				}

				if (inputs.size == 0) {
					mapping.remove(key);
				}
			}
		}
	}

	public Array<Key> getKeysForInput(ControllerInput input) {
		Array<Key> keys = cacheKeys.getNext();
		keys.clear();
		for (Entry<Key, Array<ControllerInput>> entry : mapping.entries()) {
			Array<ControllerInput> einputs = entry.value;
			Key ekey = entry.key;
			if (einputs.contains(input, false)) {
				keys.add(ekey);
			}
		}
		return keys;
	}
	
	public String getInputLabelForKey(Key key) {
		Array<ControllerInput> inputs = mapping.get(key);
		if (inputs == null) {
			return "NONE";
		}
		if (inputs.size == 1) {
			ControllerInput input = inputs.get(0);
			if (input == null) {
				return "NONE";
			}
			return input.getLabel();
		}
		return "MULTI";
	}

	public String getInputLabelForKey(Key key, Controller controller) {
		Array<ControllerInput> inputs = mapping.get(key);
		if (inputs == null) {
			return "NONE";
		}

		int num = 0;
		ControllerInput input = null;

		for (ControllerInput cinput : inputs) {
			if (cinput.controller == controller) {
				num++;
				input = cinput;
				if (num == 2) {
					break;
				}
			}
		}

		if (num == 0) {
			return "NONE";
		}
		if (num == 1) {
			return input.getLabel();
		}
		return "MULTI";
	}
	
	public void map(Key key, ControllerInput input) {
		synchronized (mapping) {
			Array<ControllerInput> inputs = mapping.get(key);
			if (inputs == null) {
				inputs = new Array<ControllerInput>(2);
				mapping.put(key, inputs);
			}
			inputs.add(input);
		}
	}
	
	public void tick() {
		//Check for new controllers and update list
		Array<Controller> controllersManaged = Controllers.getControllers();
		if (controllersManaged.size > controllersAuto.size) {
			for (Controller controller : controllersManaged) {
				if (controllersAuto.contains(controller, true)) {
					continue;
				}
				connected(controller);
			}
		} else if (controllersManaged.size < controllersAuto.size) {
			for (Controller controller : controllersManaged) {
				if (controllersManaged.contains(controller, true)) {
					continue;
				}
				disconnected(controller);
			}
		}

		//Assign key if asked for
		if (assignKey != null) {
			if (assignKeyHelper != null) {
				assignKeyHelper.text = assignKey.name+" ("+Shadow.controllerHelper.getInputLabelForKey(assignKey, assignKeyController)+") ...";
			}
			if (tmpkey != null && tmpkey != assignKey) {
				throw new Error("Switching key to assign while assigning key not supported!");
			}
			tmpkey = assignKey;
		} else {
			tmpkey = null;
		}
		
		//"Unstick" axes.
		synchronized (mapping) {
			for (Entry<Key, Array<ControllerInput>> entry : mapping.entries()) {
				Array<ControllerInput> inputs = entry.value;
				Key key = entry.key;

				for (ControllerInput input : inputs) {
					if (input instanceof ControllerAxis) {
						ControllerAxis axis = (ControllerAxis) input;
						float value = axis.controller.getAxis(axis.axisCode);
						if ((value < -deadzone && axis.negative) || (value > deadzone && !axis.negative)) {
							//Do nothing - the listener should handle this.
						} else {
							if (key.triggerer == Triggerer.CONTROLLER_AXIS && key.isDown) {
								key.nextState = false;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void connected(Controller controller) {
		if (controllers.contains(controller, true)) {
			return;
		}

		controller.addListener(this);
		controllers.add(controller);
		if (Controllers.getControllers().contains(controller, true)) {
			controllersAuto.add(controller);
		}

		String os = System.getProperty("os.name").toLowerCase();
		if (controller.getName().contains("PLAYSTATION(R)3")) {
			//Sidenote: Third-Party bluetooth PS3 controllers should use the same button mapping as the original controller.
			//Otherwise, the PS3 itself would even have problems with the button IDs being different.
			if (Input.isOuya) {
				System.out.println("Automapping PS3 controller on Ouya...");
				map(Input.up, new ControllerButton(controller, 19));
				map(Input.down, new ControllerButton(controller, 20));
				map(Input.left, new ControllerButton(controller, 21));
				map(Input.right, new ControllerButton(controller, 22));
				map(Input.up, new ControllerAxis(controller, 1, true));
				map(Input.down, new ControllerAxis(controller, 1, false));
				map(Input.left, new ControllerAxis(controller, 0, true));
				map(Input.right, new ControllerAxis(controller, 0, false));
				map(Input.jump, new ControllerButton(controller, 96));
				map(Input.pause, new ControllerButton(controller, 108));
				map(Input.pause, new ControllerButton(controller, 86));
				map(Input.enter, new ControllerButton(controller, 96));
				map(Input.androidBack, new ControllerButton(controller, 97));
			} else if ((os.equals("linux") || os.equals("unix"))) {
				System.out.println("Automapping PS3 controller on Linux...");
				map(Input.up, new ControllerButton(controller, 4));
				map(Input.down, new ControllerButton(controller, 6));
				map(Input.left, new ControllerButton(controller, 7));
				map(Input.right, new ControllerButton(controller, 5));
				map(Input.up, new ControllerAxis(controller, 1, true));
				map(Input.down, new ControllerAxis(controller, 1, false));
				map(Input.left, new ControllerAxis(controller, 0, true));
				map(Input.right, new ControllerAxis(controller, 0, false));
				map(Input.jump, new ControllerButton(controller, 14));
				map(Input.pause, new ControllerButton(controller, 3));
				map(Input.enter, new ControllerButton(controller, 14));
				map(Input.androidBack, new ControllerButton(controller, 13));
			}
		}
		if ((os.equals("linux") || os.equals("unix")) && controller.getName().contains("Sanmos TWIN SHOCK")) {
			System.out.println("Automapping Sanmos TWIN SHOCK on Linux...");
			map(Input.up, new ControllerAxis(controller, 1, true));
			map(Input.down, new ControllerAxis(controller, 1, false));
			map(Input.left, new ControllerAxis(controller, 0, true));
			map(Input.right, new ControllerAxis(controller, 0, false));
			map(Input.jump, new ControllerButton(controller, 2));
			map(Input.pause, new ControllerButton(controller, 11));
			map(Input.enter, new ControllerButton(controller, 2));
			map(Input.androidBack, new ControllerButton(controller, 3));
		}
		if (Ouya.ID.equals(controller.getName())) {
			System.out.println("Automapping Ouya controller...");
			map(Input.up, new ControllerButton(controller, Ouya.BUTTON_DPAD_UP));
			map(Input.down, new ControllerButton(controller, Ouya.BUTTON_DPAD_DOWN));
			map(Input.left, new ControllerButton(controller, Ouya.BUTTON_DPAD_LEFT));
			map(Input.right, new ControllerButton(controller, Ouya.BUTTON_DPAD_RIGHT));
			map(Input.up, new ControllerAxis(controller, Ouya.AXIS_LEFT_Y, true));
			map(Input.down, new ControllerAxis(controller, Ouya.AXIS_LEFT_Y, false));
			map(Input.left, new ControllerAxis(controller, Ouya.AXIS_LEFT_X, true));
			map(Input.right, new ControllerAxis(controller, Ouya.AXIS_LEFT_X, false));
			map(Input.jump, new ControllerButton(controller, Ouya.BUTTON_O));
			map(Input.pause, new ControllerButton(controller, Ouya.BUTTON_MENU));
			map(Input.enter, new ControllerButton(controller, Ouya.BUTTON_O));
			map(Input.androidBack, new ControllerButton(controller, Ouya.BUTTON_A));
		}
		
		refreshMapping();
	}
	
	@Override
	public void disconnected(Controller controller) {
		if (!controllers.contains(controller, true)) {
			return;
		}

		controller.removeListener(this);
		controllers.removeValue(controller, true);
		if (controllersAuto.contains(controller, true)) {
			controllersAuto.removeValue(controller, true);
		}

		refreshMapping();
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		//System.out.println("Pressed button "+buttonCode+" on controller "+controller);
		//ControllerButton button = new ControllerButton(controller, buttonCode);
		ControllerButton button = cacheButtons.getNext().set(controller, buttonCode);
		for (Key key : getKeysForInput(button)) {
			//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to true");
			key.triggerer = Triggerer.CONTROLLER_BUTTON;
			key.nextState = true;
		}
		if (assignKey != null && controller == assignKeyController) {
			System.out.println("Mapped in-game key \""+assignKey.name+"\" to button "+buttonCode+" on controller "+(controller.getName()));
			map(assignKey, new ControllerButton(button));
			refreshMapping();
			if (assignKeyHelper != null) {
				assignKeyHelper.text = assignKey.name+" ("+Shadow.controllerHelper.getInputLabelForKey(assignKey, assignKeyController)+")";
			}
			assignKey = null;
			assignKeyController = null;
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		//System.out.println("Released button "+buttonCode+" on controller "+controller);
		//ControllerButton button = new ControllerButton(controller, buttonCode);
		ControllerButton button = cacheButtons.getNext().set(controller, buttonCode);
		for (Key key : getKeysForInput(button)) {
			//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to false");
			key.tick();
			key.triggerer = Triggerer.CONTROLLER_BUTTON;
			key.nextState = false;
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		float pvalue = value;
		boolean negative = false;
		if (value < 0f) {
			pvalue = -value;
			negative = true;
		}
		//ControllerAxis axis = new ControllerAxis(controller, axisCode, negative);
		ControllerAxis axis = cacheAxes.getNext().set(controller, axisCode, negative);
		if (pvalue >= deadzone) {//Internal deadzone.
			//System.out.println("Moved axis "+axisCode+" with current value "+value+" on controller "+controller);
			for (Key key : getKeysForInput(axis)) {
				//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to true");
				key.triggerer = Triggerer.CONTROLLER_AXIS;
				key.nextState = true;
			}
			if (pvalue >= 0.3f && assignKey != null && controller == assignKeyController) {//To eliminate minor accidental movements while assigning
				System.out.println("Mapped in-game key \""+assignKey.name+"\" to axis "+axisCode+" on controller "+(controller.getName()));
				map(assignKey, new ControllerAxis(axis));
				refreshMapping();
				if (assignKeyHelper != null) {
					assignKeyHelper.text = assignKey.name+" ("+Shadow.controllerHelper.getInputLabelForKey(assignKey, assignKeyController)+")";
				}
				assignKey = null;
				assignKeyController = null;
			}
		} else {
			for (Key key : getKeysForInput(axis)) {
				//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to true");
				key.triggerer = Triggerer.CONTROLLER_AXIS;
				key.nextState = false;
			}
			//System.out.println("Moved axis "+axisCode+" with current value "+value+" on controller "+controller+" inside \"internal deadzone\"");
		}
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		//TODO Implement
		//System.out.println("Moved pov "+povCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		//TODO Implement
		//System.out.println("Moved X slider "+sliderCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		//TODO Implement
		//System.out.println("Moved Y slider "+sliderCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		//TODO Implement
		//System.out.println("Moved accelerometer "+accelerometerCode+" with current value "+value+" on controller "+controller);
		return false;
	}
	
}
