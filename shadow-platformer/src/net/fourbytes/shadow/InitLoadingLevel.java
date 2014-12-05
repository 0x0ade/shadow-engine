package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import net.fourbytes.shadow.map.Converter;
import net.fourbytes.shadow.mod.ModManager;
import net.fourbytes.shadow.network.KryoNetClient;
import net.fourbytes.shadow.network.KryoNetServer;

/**
 * The initial loading level, also known as the first loading screen.
 */
public class InitLoadingLevel extends LoadingLevel {

	public InitLoadingLevel() {
		super();
		steps = new Runnable[] {
				new Runnable() {
					public void run() {
						//Resize window
						Shadow.resize();
					}
				},
				new Runnable() {
					public void run() {
						//Load fonts
						Fonts.load();
					}
				},
				new Runnable() {
					public void run() {
						//Load textures
						Images.loadImages();
					}
				},
				new Runnable() {
					public void run() {
						//Load sounds
						Sounds.loadSounds();
					}
				},
				new Runnable() {
					public void run() {
						//Set up networking
						if (!Converter.convertOnly) {
							//TODO Set up streams
							Shadow.client = new KryoNetClient();
							Shadow.server = new KryoNetServer();
						}
					}
				},
				new Runnable() {
					public void run() {
						//Load mods (with textures) on GL thread
						if (Thread.currentThread() != Shadow.thread) {
							Gdx.app.postRunnable(this);
							return;
						}
						ModManager.loader.init(null);
						ModManager.create();
					}
				},
				new Runnable() {
					public void run() {
						//Jump into first level (IntroLevel)
						//Shadow.level = new TitleLevel();
                        Shadow.level = new IntroLevel();
					}
				}
		};
	}

}
