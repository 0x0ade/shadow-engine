package net.fourbytes.shadow;

import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.MenuLevel.MenuItem;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
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
		//Ignore, just use Controllers.getControllers() instead
	}

	@Override
	public void disconnected(Controller controller) {
		//Ignore, just use Controllers.getControllers() instead
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		//TODO
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
		//TODO
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
