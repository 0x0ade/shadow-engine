package net.fourbytes.shadow;

import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.MenuLevel.MenuItem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class SetupControllerLevel extends MenuLevel {
	
	public SetupControllerLevel() {
		this(null);
	}
	
	public SetupControllerLevel(final MenuLevel parent) {
		super(parent);
		
		Input.pause.listeners.add(this);
		
		items.add(getMenuItemFor(Input.up));
		items.add(getMenuItemFor(Input.down));
		items.add(getMenuItemFor(Input.left));
		items.add(getMenuItemFor(Input.right));
		items.add(getMenuItemFor(Input.jump));
		items.add(getMenuItemFor(Input.pause));
		items.add(getMenuItemFor(Input.enter));
		
		items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
			Shadow.level = parent;
			}}));
		
		System.gc();
	}

	protected MenuItem getMenuItemFor(final Key key) {
		final MenuItem item = new MenuItem(this, key.name+" ("+Shadow.controllerHelper.getButtonLabelForKey(key)+")", null);
		item.action = new Runnable(){public void run(){
			Shadow.controllerHelper.assignKey = key;
			Shadow.controllerHelper.assignKeyHelper = item;
			}};
		return item;
	}
	
	@Override
	public void keyDown(Key key) {
		super.keyDown(key);
		if (Shadow.level != this) {
			return;
		}
		if (key == Input.pause) {
			Shadow.controllerHelper.mapping.remove(Shadow.controllerHelper.assignKey);
			if (Shadow.controllerHelper.assignKeyHelper != null) {
				Shadow.controllerHelper.assignKeyHelper.text = Shadow.controllerHelper.assignKey.name+" ("+Shadow.controllerHelper.getButtonLabelForKey(Shadow.controllerHelper.assignKey)+")";
			}
			Shadow.controllerHelper.assignKey = null;
			Shadow.controllerHelper.assignKeyHelper = null;
		}
	}

}
