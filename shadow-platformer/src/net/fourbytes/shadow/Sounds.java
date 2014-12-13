package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.mod.AMod;
import net.fourbytes.shadow.utils.Garbage;

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
		if (sound == null) {
			sound = sounds.get("unknown");
			sounds.put(savename, sound);
		}
		return sound;
	}
	
	public static void loadSounds() {
		addSound("unknown");

		addSound("button");
	}
	
	public static void autoaddSound(String name) {
		addSound(name, "data/sounds/"+name+".wav");
	}
	
	public static void addSound(String name) {
		addSound(name, "data/sounds/"+name+".wav");
	}
	
	public static void addSound(String savename, String loadname) {
		try {
			Sound s;
			if (Gdx.audio != null) {
				s = Gdx.audio.newSound(Gdx.files.internal(loadname));
			} else {
				s = new Sound() {
					@Override
					public long play() {
						return 0;
					}

					@Override
					public long play(float volume) {
						return 0;
					}

					@Override
					public long play(float volume, float pitch, float pan) {
						return 0;
					}

					@Override
					public long loop() {
						return 0;
					}

					@Override
					public long loop(float volume) {
						return 0;
					}

					@Override
					public long loop(float volume, float pitch, float pan) {
						return 0;
					}

					@Override
					public void stop() {

					}

					@Override
					public void pause() {

					}

					@Override
					public void resume() {

					}

					@Override
					public void dispose() {

					}

					@Override
					public void stop(long soundId) {

					}

					@Override
					public void pause(long soundId) {

					}

					@Override
					public void resume(long soundId) {

					}

					@Override
					public void setLooping(long soundId, boolean looping) {

					}

					@Override
					public void setPitch(long soundId, float pitch) {

					}

					@Override
					public void setVolume(long soundId, float volume) {

					}

					@Override
					public void setPan(long soundId, float pan, float volume) {

					}

					@Override
					public void setPriority(long soundId, int priority) {

					}
				};
			}
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
		Rectangle vp = Shadow.cam.camrec;

		Vector2 origpos = Garbage.vec2s.getNext();
		origpos.set(vp.x + vp.width/2f, vp.y + vp.height/2f);
		if (Shadow.level != null && Shadow.level.player != null) {
			origpos.set(Shadow.level.player.pos);
			origpos.add(Shadow.level.player.rec.width/2f, Shadow.level.player.rec.height/2f);
		}

		float maxdistsq = vp.width;
		if (vp.height < vp.width) {
			maxdistsq = vp.height;
		}
		maxdistsq *= maxdistsq;

		float distx = pos.x - origpos.x;
		float disty = pos.y - origpos.y;
		float distsq = distx*distx + disty * disty;

        float vol = 0f;
        if (distsq <= maxdistsq) {
            vol = 0.5f * MathUtils.cos(distsq * (MathUtils.PI / maxdistsq)) + 0.5f;
        }

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
