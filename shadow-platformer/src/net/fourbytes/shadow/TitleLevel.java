package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import net.fourbytes.shadow.genlevel.GenLevel;

public class TitleLevel extends MenuLevel {

	public TitleLevel() {
		this(null);
	}

	public TitleLevel(MenuLevel parent) {
		super(parent);

		items.add(new MenuItem(this, "Start GenLevel Test", new Runnable(){public void run(){
			Shadow.level = new GenLevel();
			Shadow.cam.firsttick = true;
		}}));
		items.add(new MenuItem(this, "Start Test", new Runnable(){public void run(){
			//Shadow.level = new Level("test");

			final LoadingLevel loadinglevel = new LoadingLevel();
			loadinglevel.steps = new Runnable[] {
					new Runnable() {
						public void run() {
							Shadow.level = new Level("test", loadinglevel);
						}
					}
			};
			Shadow.level = loadinglevel;
			loadinglevel.start();

			Shadow.cam.firsttick = true;
		}}));
        items.add(new MenuItem(this, "Multiplayer", new Runnable(){public void run(){
            Shadow.level = new MultiplayerMenuLevel(TitleLevel.this);
            Shadow.cam.firsttick = true;
        }}));
		items.add(new MenuItem(this, "Options", new Runnable(){public void run(){
			Shadow.level = new OptionsMenuLevel(TitleLevel.this);
			Shadow.cam.firsttick = true;
		}}));
		items.add(new MenuItem(this, "Exit Game", new Runnable(){public void run(){
			Gdx.app.exit();
		}}));
		
		if (getClass().equals(TitleLevel.class)) {
			Shadow.cam.bg = Background.getDefault();
		}
		
		ready = true;
	}

}
