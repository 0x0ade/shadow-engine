package net.fourbytes.shadow;

import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.systems.INextDay;
import net.fourbytes.shadow.systems.ITimeDaySystem;
import net.fourbytes.shadow.systems.IWeatherSystem;

public class PauseLevel extends TitleLevel {

	public PauseLevel() {
		this(null);
	}

	public PauseLevel(MenuLevel parent) {
		super(parent);
		
		Array<MenuItem> items = new Array<MenuItem>(MenuItem.class);
		
		items.add(new MenuItem(this, "Continue", new Runnable(){public void run(){
			Shadow.level = bglevel;
			}}));
		items.add(new MenuItem(this, "Save", new Runnable(){public void run(){
			//TODO
			final LoadingLevel loadinglevel = new LoadingLevel();
			loadinglevel.bglevel = bglevel;
			loadinglevel.steps = new Runnable[] {
					new Runnable() {
						public void run() {
							ShadowMap map = ShadowMap.createNewFrom(bglevel, loadinglevel);
							loadinglevel.progress = 0;
							loadinglevel.progressMax = 0;
							map.save(Shadow.getDir("saves").child("map.smf"));
							Shadow.level = bglevel;
						}
					}
			};
			Shadow.level = loadinglevel;
			loadinglevel.start();
			}}));
		items.add(new MenuItem(this, "Load", new Runnable(){public void run(){
			//TODO
			final LoadingLevel loadinglevel = new LoadingLevel();
			loadinglevel.bglevel = bglevel;
			loadinglevel.steps = new Runnable[] {
					new Runnable() {
						public void run() {
							loadinglevel.progress = 0;
							loadinglevel.progressMax = bglevel.mainLayer.blocks.size + bglevel.mainLayer.entities.size;
							Layer layer = bglevel.mainLayer;
							while (layer.blocks.size > 0) {
								layer.blocks.items[0].layer.remove(layer.blocks.items[0]);
								loadinglevel.progress++;
							}
							while (layer.entities.size > 0) {
								layer.entities.items[0].layer.remove(layer.entities.items[0]);
								loadinglevel.progress++;
							}

							loadinglevel.progress = 0;
							loadinglevel.progressMax = 0;
							ShadowMap map = ShadowMap.loadFile(Shadow.getDir("saves").child("map.smf"));
							map.fillLevel(bglevel, loadinglevel);
							Shadow.level = bglevel;
						}
					}
			};
			Shadow.level = loadinglevel;
			loadinglevel.start();
			}}));
		items.add(new MenuItem(this, "Main Menu", new Runnable(){public void run(){
			Shadow.level = new TitleLevel();
			}}));
		
		items.addAll(this.items);
		for (MenuItem item : this.items) {
			if (item.text.toLowerCase().startsWith("start ") || item.text.equals("Multiplayer")) {
				items.removeValue(item, true);
			}
		}
		this.items = items;
		
		ready = true;
	}

	/*
	@Override
	public void keyDown(Input.Key key) {
		if (Shadow.level != this) {
			return;
		}
		if (key == Input.androidBack) {
			Shadow.level = new TitleLevel();
		} else {
			super.keyDown(key);
		}
	}
	*/
	
}
