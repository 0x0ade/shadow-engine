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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Sounds {
	private final static ObjectMap<String, Sound> sounds = new ObjectMap<String, Sound>();
	
	public static void addSound(String savename, Sound s) {
		sounds.put(savename, s);
	}
	
	public static Sound getSound(String savename) {
		Sound sound = sounds.get(savename);
		if (sound == null) {
			autoaddSound(savename);
			sound = sounds.get(savename);
		}
		return sound;
	}
	
	public static void loadSounds() {
		addSound("button");
		addSound("disappear");
		addSound("jump");
		addSound("point");
		addSound("button_ingame");
	}
	
	public static void autoaddSound(String name) {
		addSound(name, "data/sounds/"+name+".wav");
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
	
	public static float calcVolume(Vector2 pos) {
		float vol = 1f;
		Rectangle vp = Shadow.cam.camrec;
		if (!vp.contains(pos.x, pos.y)) {
			return 0f;
		}
		Vector2 orgpos = Garbage.vec2;
		orgpos.set(0, 0);
		if (Shadow.level != null && Shadow.level.player != null) {
			orgpos.set(Shadow.level.player.pos);
			orgpos.add(Shadow.level.player.rec.width/2f, Shadow.level.player.rec.height/2f);
		}
		float xdiff = pos.x - orgpos.x;
		if (xdiff < 0) xdiff = -xdiff;
		float ydiff = pos.y - orgpos.y;
		if (ydiff < 0) ydiff = -ydiff;
		float xrad = vp.width/4f;
		float xvol = xdiff / xrad;
		if (xvol > 1f) xvol = 1f;
		xvol = 1f - xvol;
		float yrad = vp.height/4f;
		float yvol = ydiff / yrad;
		if (yvol > 1f) yvol = 1f;
		yvol = 1f - yvol;
		
		vol = (xvol + yvol) / 2f;
		
		return vol;
	}

	public static float calcPitch(float base, float radius) {
		float result = base;
		float diff = (float)Math.random()*(radius/2f);
		diff -= radius/4f;
		result += diff;
		return result;
	}
}
