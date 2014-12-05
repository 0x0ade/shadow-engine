package net.fourbytes.shadow;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.Key.Triggerer;
import net.fourbytes.shadow.MenuLevel.MenuItem;
import net.fourbytes.shadow.utils.Cache;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;
import net.fourbytes.shadow.utils.backend.ControllerNumerator;

/**
 * This class manages all controllers connected to the current
 * application, handles their input and translates them into
 * Shadow key presses. Due to the nature of GDX's Controllers
 * class and it's ControllerManager not managing custom controllers,
 * such as the controllers created by a GDXRemote server, it is
 * advised to use this class to poll for currently connected
 * controllers. Also, this class manages mapping with help of
 * the ControllerHelper.ControllerInput class and it's subclasses
 * ControllerHelper.ControllerButton and ControllerHelper.ControllerAxis.
 * POVs, accelerators and more are to come.
 * <br><br>
 * This class is instance of ControllerListener but uses polling
 * of Controllers' ControllerManager's controllers. Custom
 * controllers may get added into the list of the Controllers'
 * ControllerManager's controllers but must add the default
 * instance of this class (sitting in Shadow by default) to the
 * list of their ControllerListeners manually and load the
 * mapping manually by calling connected manually.
 * <br><br>
 * Possible automatic mappings of the controller buttons to in-game buttons
 * may happen by creating a new ControllerHelper.Mapping subclass and adding
 * an instance of that class into the mappings array of the current
 * ControllerHelper instance. Default mappings for OUYA, PS3 (Linux and
 * OUYA only) and Sanmos (Linux only) controllers are given.
 */
public final class ControllerHelper implements ControllerListener {

	//MAPPINGS (may move into separate package in future)

	public static abstract interface Mapping {
		public String getName();

		/**
		 * @return true if mapped; false otherwise
		 */
		public boolean map(Controller controller, ControllerHelper controllerHelper);
	}

    public static class ConfigMapping implements Mapping {
        @Override
        public String getName() {
            return "User-created mapping loaded from file";
        }

        @Override
        public boolean map(Controller controller, ControllerHelper controllerHelper) {
            String jsonString = Options.getString("controller.\""+controller.getName()+"\"", null);

            if (jsonString == null) {
                return false;
            }

            JsonValue json = Garbage.jsonReader.parse(jsonString);

            JsonValue axes = json.get("axes");
            for (JsonValue axis = axes.child; axis != null; axis = axis.next) {
                int id = Integer.parseInt(axis.name);
                boolean negative = axis.name.startsWith("-");
                if (negative) {
                    id = -id;
                }
                for (JsonValue key = axis.child; key != null; key = key.next) {
                    String keyName = key.asString();
                    Key keyInput = Input.getKey(keyName);
                    ControllerAxis axisController = new ControllerAxis(controller, id, negative);
                    controllerHelper.map(keyInput, axisController);
                }
            }

            JsonValue buttons = json.get("buttons");
            for (JsonValue button = buttons.child; button != null; button = button.next) {
                int id = Integer.parseInt(button.name);
                for (JsonValue key = button.child; key != null; key = key.next) {
                    String keyName = key.asString();
                    Key keyInput = Input.getKey(keyName);
                    ControllerButton axisController = new ControllerButton(controller, id);
                    controllerHelper.map(keyInput, axisController);
                }
            }

            return true;
        }
    }

	public static class OuyaMapping implements Mapping {
		@Override
		public String getName() {
			return "Default OUYA mapping";
		}

		@Override
		public boolean map(Controller controller, ControllerHelper controllerHelper) {
			if (Ouya.ID.equals(controller.getName())) {
				System.out.println("Automapping OUYA controller...");
				controllerHelper.map(Input.up, new ControllerButton(controller, Ouya.BUTTON_DPAD_UP));
				controllerHelper.map(Input.down, new ControllerButton(controller, Ouya.BUTTON_DPAD_DOWN));
				controllerHelper.map(Input.left, new ControllerButton(controller, Ouya.BUTTON_DPAD_LEFT));
				controllerHelper.map(Input.right, new ControllerButton(controller, Ouya.BUTTON_DPAD_RIGHT));
				controllerHelper.map(Input.up, new ControllerAxis(controller, Ouya.AXIS_LEFT_Y, true));
				controllerHelper.map(Input.down, new ControllerAxis(controller, Ouya.AXIS_LEFT_Y, false));
				controllerHelper.map(Input.left, new ControllerAxis(controller, Ouya.AXIS_LEFT_X, true));
				controllerHelper.map(Input.right, new ControllerAxis(controller, Ouya.AXIS_LEFT_X, false));
				controllerHelper.map(Input.jump, new ControllerButton(controller, Ouya.BUTTON_O));
				controllerHelper.map(Input.pause, new ControllerButton(controller, Ouya.BUTTON_MENU));
				controllerHelper.map(Input.enter, new ControllerButton(controller, Ouya.BUTTON_O));
				controllerHelper.map(Input.androidBack, new ControllerButton(controller, Ouya.BUTTON_A));
				return true;
			}
			return false;
		}
	}

	public static class PS3Mapping implements Mapping {
		@Override
		public String getName() {
			return "Default PS3 mapping";
		}

		@Override
		public boolean map(Controller controller, ControllerHelper controllerHelper) {
			String os = System.getProperty("os.name").toLowerCase();
			if (controller.getName().contains("PLAYSTATION(R)3")) {
				//Sidenote: Third-Party bluetooth PS3 controllers should use the same button mapping as the original controller.
				//Otherwise, the PS3 itself would even have problems with the button IDs being different.
				if (Input.isOuya) {
					System.out.println("Automapping PS3 controller on Ouya...");
					controllerHelper.map(Input.up, new ControllerButton(controller, 19));
					controllerHelper.map(Input.down, new ControllerButton(controller, 20));
					controllerHelper.map(Input.left, new ControllerButton(controller, 21));
					controllerHelper.map(Input.right, new ControllerButton(controller, 22));
					controllerHelper.map(Input.up, new ControllerAxis(controller, 1, true));
					controllerHelper.map(Input.down, new ControllerAxis(controller, 1, false));
					controllerHelper.map(Input.left, new ControllerAxis(controller, 0, true));
					controllerHelper.map(Input.right, new ControllerAxis(controller, 0, false));
					controllerHelper.map(Input.jump, new ControllerButton(controller, 96));
					controllerHelper.map(Input.pause, new ControllerButton(controller, 108));
					controllerHelper.map(Input.pause, new ControllerButton(controller, 86));
					controllerHelper.map(Input.enter, new ControllerButton(controller, 96));
					controllerHelper.map(Input.androidBack, new ControllerButton(controller, 97));
					return true;
				} else if ((os.equals("linux") || os.equals("unix"))) {
					System.out.println("Automapping PS3 controller on Linux...");
					controllerHelper.map(Input.up, new ControllerButton(controller, 4));
					controllerHelper.map(Input.down, new ControllerButton(controller, 6));
					controllerHelper.map(Input.left, new ControllerButton(controller, 7));
					controllerHelper.map(Input.right, new ControllerButton(controller, 5));
					controllerHelper.map(Input.up, new ControllerAxis(controller, 1, true));
					controllerHelper.map(Input.down, new ControllerAxis(controller, 1, false));
					controllerHelper.map(Input.left, new ControllerAxis(controller, 0, true));
					controllerHelper.map(Input.right, new ControllerAxis(controller, 0, false));
					controllerHelper.map(Input.jump, new ControllerButton(controller, 14));
					controllerHelper.map(Input.pause, new ControllerButton(controller, 3));
					controllerHelper.map(Input.enter, new ControllerButton(controller, 14));
					controllerHelper.map(Input.androidBack, new ControllerButton(controller, 13));
					return true;
				}
			}
			return false;
		}
	}

	public static class SanmosMapping implements Mapping {
		@Override
		public String getName() {
			return "Default Sanmos mapping";
		}

		@Override
		public boolean map(Controller controller, ControllerHelper controllerHelper) {
			String os = System.getProperty("os.name").toLowerCase();
			if ((os.equals("linux") || os.equals("unix")) && controller.getName().contains("Sanmos TWIN SHOCK")) {
				System.out.println("Automapping Sanmos TWIN SHOCK on Linux...");
				controllerHelper.map(Input.up, new ControllerAxis(controller, 1, true));
				controllerHelper.map(Input.down, new ControllerAxis(controller, 1, false));
				controllerHelper.map(Input.left, new ControllerAxis(controller, 0, true));
				controllerHelper.map(Input.right, new ControllerAxis(controller, 0, false));
				controllerHelper.map(Input.jump, new ControllerButton(controller, 2));
				controllerHelper.map(Input.pause, new ControllerButton(controller, 11));
				controllerHelper.map(Input.enter, new ControllerButton(controller, 2));
				controllerHelper.map(Input.androidBack, new ControllerButton(controller, 3));
				return true;
			}
			return false;
		}
	}

	//INPUTS

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

	//CACHES

	protected final static Cache<Array<Key>> cacheKeys = new Cache(Array.class, 32,
			new Object[] {false, 4, Key.class}, new Class[] {boolean.class, int.class, Class.class});
	protected final static Cache<ControllerButton> cacheButtons = new Cache<ControllerButton>(ControllerButton.class, 64);
	protected final static Cache<ControllerAxis> cacheAxes = new Cache<ControllerAxis>(ControllerAxis.class, 64);

	//CONTROLLERHELPER

	public static float deadzone = 0.25f;

	public final ObjectMap<Key, Array<ControllerInput>> keymap = new ObjectMap<Key, Array<ControllerInput>>();
	public final ObjectMap<Key, Array<ControllerInput>> tmpmap = new ObjectMap<Key, Array<ControllerInput>>();
    public final ObjectMap<Controller, JsonValue> tmpconfmap = new ObjectMap<Controller, JsonValue>();

	public final Array<Mapping> mappings = new Array<Mapping>(Mapping.class);
	
	private Key tmpkey;
	public Key assignKey;
	public MenuItem assignKeyHelper;
	public Controller assignKeyController;

	public Array<Controller> controllers = new Array<Controller>(Controller.class);
	public Array<Controller> controllersAuto = new Array<Controller>(Controller.class);

	public ControllerNumerator numerator;

	public ControllerHelper() {
        mappings.add(new ConfigMapping());
		if (!Input.isOuya) {//The OUYA has got it's own mapping for the OUYA controller since OUYA Everywhere.
			mappings.add(new OuyaMapping());
		}
		mappings.add(new PS3Mapping());
		mappings.add(new SanmosMapping());
	}

	public void refreshKeymap() {
		tmpmap.clear();
        tmpconfmap.clear();
		synchronized (keymap) {
            for (Controller controller : controllers) {
                JsonValue json = new JsonValue(JsonValue.ValueType.object);

                json.child = new JsonValue(JsonValue.ValueType.object);
                json.child.name = "buttons";

                json.child.prev = new JsonValue(JsonValue.ValueType.object);
                json.child.prev.next = json.child;
                json.child = json.child.prev;
                json.child.name = "axes";

                tmpconfmap.put(controller, json);
            }

			for (Entry<Key, Array<ControllerInput>> entry : keymap.entries()) {
				Array<ControllerInput> inputs = entry.value;
				Key key = entry.key;

				for (ControllerInput input : inputs) {
					if (!controllers.contains(input.controller, true)) {
						inputs.removeValue(input, false);
                        continue;
					}

                    JsonValue json = tmpconfmap.get(input.controller);
                    if (input instanceof ControllerAxis) {
                        JsonValue axes = json.get("axes");
                        JsonValue axis = axes.get((((ControllerAxis) input).negative?"-":"") + ((ControllerAxis) input).axisCode);

                        if (axis == null) {
                            axis = new JsonValue(JsonValue.ValueType.array);
                            axis.name = (((ControllerAxis) input).negative?"-":"") + ((ControllerAxis) input).axisCode;
                            if (axes.child == null) {
                                axes.child = axis;
                            } else {
                                JsonValue child = axes.child;
                                axes.child = axis;
                                child.prev = axis;
                                axis.next = child;
                            }
                        }

                        JsonValue keyValue = new JsonValue(key.name);
                        keyValue.name = "";
                        if (axis.child == null) {
                            axis.child = keyValue;
                        } else {
                            JsonValue child = axis.child;
                            axis.child = keyValue;
                            child.prev = keyValue;
                            keyValue.next = child;
                        }
                    } else if (input instanceof ControllerButton) {
                        JsonValue buttons = json.get("buttons");
                        JsonValue button = buttons.get("" + ((ControllerButton) input).buttonCode);

                        if (button == null) {
                            button = new JsonValue(JsonValue.ValueType.array);
                            button.name = "" + ((ControllerButton) input).buttonCode;
                            if (buttons.child == null) {
                                buttons.child = button;
                            } else {
                                JsonValue child = buttons.child;
                                buttons.child = button;
                                child.prev = button;
                                button.next = child;
                            }
                        }

                        JsonValue keyValue = new JsonValue(key.name);
                        keyValue.name = "";
                        if (button.child == null) {
                            button.child = keyValue;
                        } else {
                            JsonValue child = button.child;
                            button.child = keyValue;
                            child.prev = keyValue;
                            keyValue.next = child;
                        }
                    }
				}

				if (inputs.size == 0) {
					keymap.remove(key);
				}
			}

            for (Entry<Controller, JsonValue> entry : tmpconfmap.entries()) {
                Controller controller = entry.key;
                JsonValue json = entry.value;
                String jsonString = json.toString();
                Options.putString("controller.\""+controller.getName()+"\"", jsonString);
            }

            Options.flush();
		}
	}

	public Array<Key> getKeysForInput(ControllerInput input) {
		Array<Key> keys = cacheKeys.getNext();
		keys.clear();
		for (Entry<Key, Array<ControllerInput>> entry : keymap.entries()) {
			Array<ControllerInput> einputs = entry.value;
			Key ekey = entry.key;
			if (einputs.contains(input, false)) {
				keys.add(ekey);
			}
		}
		return keys;
	}
	
	public String getInputLabelForKey(Key key) {
		Array<ControllerInput> inputs = keymap.get(key);
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
        if (inputs.size == 0) {
            return "NONE";
        }
		return "MULTI";
	}

	public String getInputLabelForKey(Key key, Controller controller) {
		Array<ControllerInput> inputs = keymap.get(key);
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
		synchronized (keymap) {
			Array<ControllerInput> inputs = keymap.get(key);
			if (inputs == null) {
				inputs = new Array<ControllerInput>(true, 2, ControllerInput.class);
				keymap.put(key, inputs);
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
		synchronized (keymap) {
			for (Entry<Key, Array<ControllerInput>> entry : keymap.entries()) {
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

		for (Mapping mapping : mappings) {
			if (mapping.map(controller, this)) {
				break;
			}
		}

		refreshKeymap();
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

		refreshKeymap();
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
			refreshKeymap();
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
				refreshKeymap();
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
