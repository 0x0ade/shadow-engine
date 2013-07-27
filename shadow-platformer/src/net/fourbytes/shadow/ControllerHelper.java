package net.fourbytes.shadow;

import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.MenuLevel.MenuItem;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public final class ControllerHelper implements ControllerListener {
	
	public static class ControllerButton {
		
		public Controller controller;
		public int buttonCode;
		
		public ControllerButton(Controller controller, int buttonCode) {
			this.controller = controller;
			this.buttonCode = buttonCode;
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
	}

	public ObjectMap<Key, ControllerButton> mapping = new ObjectMap<Key, ControllerButton>();
	public ObjectMap<Key, ControllerButton> tmpmap = new ObjectMap<Key, ControllerButton>();
	
	private Key tmpkey;
	public Key assignKey;
	public MenuItem assignKeyHelper;
	
	public ControllerHelper() {
		for (Controller controller : Controllers.getControllers()) {
			connected(controller);
		}
	}
	
	public void refreshMapping() {
		tmpmap.clear();
		Array<Controller> controllers = Controllers.getControllers();
		for (Entry<Key, ControllerButton> entry : mapping.entries()) {
			ControllerButton button = entry.value;
			Key key = entry.key;
			
			if (!controllers.contains(button.controller, true)) {
				mapping.remove(key);
				continue;
			}
		}
	}
	
	public Array<Key> getKeysForButton(ControllerButton button) {
		Array<Key> keys = new Array<Key>();
		for (Entry<Key, ControllerButton> entry : mapping.entries()) {
			ControllerButton ebutton = entry.value;
			Key ekey = entry.key;
			if (button.equals(ebutton)) {
				keys.add(ekey);
			}
		}
		return keys;
	}
	
	public String getButtonLabelForKey(Key key) {
		ControllerButton button = mapping.get(key);
		if (button == null) {
			return "NONE";
		}
		String label = button.buttonCode+""; //TODO
		return label;
	}
	
	public void tick() {
		if (assignKey != null) {
			if (assignKeyHelper != null) {
				assignKeyHelper.text = assignKey.name+" ("+Shadow.controllerHelper.getButtonLabelForKey(assignKey)+") ...";
			}
			if (tmpkey != null && tmpkey != assignKey) {
				throw new Error("Switching key to assign while assigning key not supported!");
			}
			tmpkey = assignKey;
		} else {
			tmpkey = null;
		}
	}
	
	@Override
	public void connected(Controller controller) {
		String os = System.getProperty("os.name").toLowerCase();
		if ((os.equals("linux") || os.equals("unix")) && controller.getName().contains("PLAYSTATION(R)3")) {
			//Sidenote: Third-Party bluetooth PS3 controllers should use the same button mapping as the original controller. 
			//Otherwise, the PS3 itself would even have problems with the button IDs being different.
			System.out.println("Automapping PS3 controller on Linux...");
			//TODO
			mapping.put(Input.up, new ControllerButton(controller, 4));
			mapping.put(Input.down, new ControllerButton(controller, 6));
			mapping.put(Input.left, new ControllerButton(controller, 7));
			mapping.put(Input.right, new ControllerButton(controller, 5));
			mapping.put(Input.jump, new ControllerButton(controller, 14));
			mapping.put(Input.pause, new ControllerButton(controller, 3));
			mapping.put(Input.enter, new ControllerButton(controller, 14));
			mapping.put(Input.androidBack, new ControllerButton(controller, 13));
		}
		if (Ouya.ID.equals(controller.getName())) {
			System.out.println("Automapping Ouya controller...");
			//TODO
			mapping.put(Input.up, new ControllerButton(controller, -1));
			mapping.put(Input.down, new ControllerButton(controller, -1));
			mapping.put(Input.left, new ControllerButton(controller, -1));
			mapping.put(Input.right, new ControllerButton(controller, -1));
			mapping.put(Input.jump, new ControllerButton(controller, Ouya.BUTTON_O));
			mapping.put(Input.pause, new ControllerButton(controller, Ouya.BUTTON_MENU));
			mapping.put(Input.enter, new ControllerButton(controller, Ouya.BUTTON_O));
			mapping.put(Input.androidBack, new ControllerButton(controller, Ouya.BUTTON_A));
		}
		
		refreshMapping();
	}
	
	@Override
	public void disconnected(Controller controller) {
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		//System.out.println("Pressed button "+buttonCode+" on controller "+controller);
		ControllerButton button = new ControllerButton(controller, buttonCode);
		for (Key key : getKeysForButton(button)) {
			//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to true");
			key.nextState = true;
		}
		if (assignKey != null) {
			System.out.println("Mapped in-game key \""+assignKey.name+"\" to button "+buttonCode+" on controller "+controller);
			mapping.put(assignKey, button);
			refreshMapping();
			if (assignKeyHelper != null) {
				assignKeyHelper.text = assignKey.name+" ("+Shadow.controllerHelper.getButtonLabelForKey(assignKey)+")";
			}
			assignKey = null;
		}
		return false;
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		//System.out.println("Released button "+buttonCode+" on controller "+controller);
		ControllerButton button = new ControllerButton(controller, buttonCode);
		for (Key key : getKeysForButton(button)) {
			//System.out.println("ControllerHelper triggered key \""+key.name+"\"'s nextstate to false");
			key.nextState = false;
		}
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		//TODO
		//System.out.println("Moved axis "+axisCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		//TODO
		//System.out.println("Moved pov "+povCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		//TODO
		//System.out.println("Moved X slider "+sliderCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		//TODO
		//System.out.println("Moved Y slider "+sliderCode+" with current value "+value+" on controller "+controller);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		//TODO
		//System.out.println("Moved accelerometer "+accelerometerCode+" with current value "+value+" on controller "+controller);
		return false;
	}
	
}
