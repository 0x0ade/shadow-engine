package net.fourbytes.shadow;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import net.fourbytes.shadow.mod.AMod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Sounds {
	private static ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	
	public static void addSound(String savename, Sound s) {
		sounds.put(savename, s);
	}
	
	public static Sound getSound(String savename) {
		return sounds.get(savename);
	}
	
	public static void loadSounds() {
		addSound("button");
		addSound("disappear");
		addSound("jump");
		addSound("point");
		addSound("button_ingame");
	}
	
	public static void addSound(String name) {
		addSound(name, "data/sounds/"+name+".wav");
	}
	
	public static void addSound(String savename, String loadname) {
		try {
			Sound s = Gdx.audio.newSound(Gdx.files.internal(loadname));
			addSound(savename, s);
		} catch (Throwable t) {
			System.err.println("Loading failed: SN: "+savename+"; LN: "+loadname);
			t.printStackTrace();
		}
	}

	public static void addSoundByMod(AMod mod, String savename, String loadname) {
		try {
			throw new Throwable("IMPOSSIBRUUU");
		} catch (Throwable t) {
			System.err.println("Loading failed (from mod): SN: "+savename);
			t.printStackTrace();
		}
	}
}
