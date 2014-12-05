package net.fourbytes.shadow;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Input.Key;

public class SetupControllerLevel extends MenuLevel {

	public static Controller controller;

	public SetupControllerLevel() {
		this(null);
	}
	
	public SetupControllerLevel(final MenuLevel parent) {
		super(parent);
		showtitle = false;
		
		//Input.keylisteners.add(this);

		if (controller == null) {
			controller = Shadow.controllerHelper.numerator.getNextController(0);
		}

		if (controller == null) {
			addMenuItemsFailed();
		} else {
			addMenuItemsMapping();
		}

		ready = true;
	}

	protected void addMenuItemsFailed() {
		removeMenuItems();

		final MenuItem item = new MenuItem(this, "No compatible controller found",
				new Runnable() {
					@Override
					public void run() {
					}
				});
		items.add(item);

		items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
			Shadow.level = parent;
		}}));
	}

	protected void addMenuItemsMapping() {
		removeMenuItems();

		items.add(getMenuItemForCurrentController());
		items.add(getMenuItemFor(Input.up));
		items.add(getMenuItemFor(Input.down));
		items.add(getMenuItemFor(Input.left));
		items.add(getMenuItemFor(Input.right));
		items.add(getMenuItemFor(Input.jump));
		items.add(getMenuItemFor(Input.pause));
		items.add(getMenuItemFor(Input.enter));
		items.add(getMenuItemFor(Input.androidBack));

		items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
			Shadow.level = parent;
		}}));
	}

	protected void addMenuItemsControllers() {
		removeMenuItems();

		for (Controller controller : Shadow.controllerHelper.controllers) {
			items.add(getMenuItemFor(controller));
		}
	}

	protected void removeMenuItems() {
		if (Shadow.controllerHelper.assignKey != null) {
			Shadow.controllerHelper.assignKey = null;
			Shadow.controllerHelper.assignKeyHelper = null;
			Shadow.controllerHelper.assignKeyController = null;
		}

		items.clear();
	}

	protected MenuItem getMenuItemFor(final Key key) {
		final MenuItem item = new MenuItem(this, key.name+" ("+
				Shadow.controllerHelper.getInputLabelForKey(key, controller)+")", null);
		item.action = new Runnable(){public void run(){
			Shadow.controllerHelper.assignKey = key;
			Shadow.controllerHelper.assignKeyHelper = item;
			Shadow.controllerHelper.assignKeyController = controller;
			}};
		return item;
	}

	protected MenuItem getMenuItemFor(final Controller controller) {
		final MenuItem item = new MenuItem(this, controller.getName()+" (#"+
				(Shadow.controllerHelper.numerator.getPlayerForController(controller)+1)+")", null);
		item.action = new Runnable(){public void run(){
			SetupControllerLevel.controller = controller;
			addMenuItemsMapping();
		}};
		return item;
	}

	protected MenuItem getMenuItemForCurrentController() {
		final MenuItem item = new MenuItem(this, controller.getName()+" (#"+
				(Shadow.controllerHelper.numerator.getPlayerForController(controller)+1)+")", null);
		item.action = new Runnable(){public void run(){
			addMenuItemsControllers();
		}};
		return item;
	}
	
	@Override
	public void keyDown(Key key) {
		if (Shadow.level != this) {
			return;
		}
		if (Shadow.controllerHelper.assignKey != null) {
			if (key == Input.pause || key == Input.androidBack) {
				Array<ControllerHelper.ControllerInput> keymap =
						Shadow.controllerHelper.keymap.get(Shadow.controllerHelper.assignKey);
                if (keymap != null) {
                    for (ControllerHelper.ControllerInput input : keymap) {
                        if (input.controller == Shadow.controllerHelper.assignKeyController) {
                            keymap.removeValue(input, false);
                        }
                    }
                }
				if (Shadow.controllerHelper.assignKeyHelper != null) {
					Shadow.controllerHelper.assignKeyHelper.text = Shadow.controllerHelper.assignKey.name+" ("+Shadow.controllerHelper.getInputLabelForKey(Shadow.controllerHelper.assignKey)+")";
				}
				Shadow.controllerHelper.assignKey = null;
				Shadow.controllerHelper.assignKeyHelper = null;
				Shadow.controllerHelper.assignKeyController = null;
			}
			return;
		}
		super.keyDown(key);
	}

}
