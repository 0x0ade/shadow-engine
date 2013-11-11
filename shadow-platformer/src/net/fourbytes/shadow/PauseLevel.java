package net.fourbytes.shadow;

import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.map.ShadowMap;

public class PauseLevel extends TitleLevel {

	public PauseLevel() {
		this(null);
	}

	public PauseLevel(MenuLevel parent) {
		super(parent);
		
		Array<MenuItem> items = new Array<MenuItem>();
		
		items.add(new MenuItem(this, "Continue", new Runnable(){public void run(){
			Shadow.level = bglevel;
			}}));
		items.add(new MenuItem(this, "Save", new Runnable(){public void run(){
			//TODO
			ShadowMap map = ShadowMap.createFrom(bglevel);
			map.save(Shadow.getDir("saves").child("map.smf"));
			Shadow.level = bglevel;
			}}));
		items.add(new MenuItem(this, "Load", new Runnable(){public void run(){
			//TODO
			for (Layer layer : bglevel.layers.values()) {
				while (layer.blocks.size > 0) {
					for (Block b : layer.blocks) {
						layer.remove(b);
					}
				}
				while (layer.entities.size > 0) {
					for (Entity e : layer.entities) {
						layer.remove(e);
					}
				}
			}
			
			ShadowMap map = ShadowMap.loadFile(Shadow.getDir("saves").child("map.smf"));
			map.fillLevel(bglevel);
			Shadow.level = bglevel;
			}}));
		items.add(new MenuItem(this, "Main Menu", new Runnable(){public void run(){
			Shadow.level = new TitleLevel();
			}}));
		
		items.addAll(this.items);
		for (MenuItem item : this.items) {
			if (item.text.toLowerCase().startsWith("start ")) {
				items.removeValue(item, true);
			}
		}
		this.items = items;
		
		hasvoid = false;
		ready = true;
	}
	
}
