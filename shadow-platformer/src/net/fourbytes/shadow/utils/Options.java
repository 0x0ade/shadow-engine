package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import net.fourbytes.shadow.Background;
import net.fourbytes.shadow.Camera;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.systems.LightSystemHelper;

import java.util.Map;

public final class Options {

	private Options() {
	}

	public static Preferences back;

	//Static links

	public static void putBoolean(String key, boolean val) {
		back.putBoolean(key, val);
	}

	public static void putInteger(String key, int val) {
		back.putInteger(key, val);
	}

	public static void putLong(String key, long val) {
		back.putLong(key, val);
	}

	public static void putFloat(String key, float val) {
		back.putFloat(key, val);
	}

	public static void putString(String key, String val) {
		back.putString(key, val);
	}

	public static void put(Map<String, ?> vals) {
		back.put(vals);
	}

	public static boolean getBoolean(String key) {
		return back.getBoolean(key);
	}

	public static int getInteger(String key) {
		return back.getInteger(key);
	}

	public static long getLong(String key) {
		return back.getLong(key);
	}

	public static float getFloat(String key) {
		return back.getFloat(key);
	}

	public static String getString(String key) {
		return back.getString(key);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return back.getBoolean(key, defValue);
	}

	public static int getInteger(String key, int defValue) {
		return back.getInteger(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		return back.getLong(key, defValue);
	}

	public static float getFloat(String key, float defValue) {
		return back.getFloat(key, defValue);
	}

	public static String getString(String key, String defValue) {
		return back.getString(key, defValue);
	}

	public static Map<String, ?> get() {
		return back.get();
	}

	public static boolean contains(String key) {
		return back.contains(key);
	}

	public static void clear() {
		back.clear();
	}

	public static void remove(String key) {
		back.remove(key);
	}

	public static void flush() {
		back.flush();

		for (String key : back.get().keySet()) {
			if (key.equals("gfx.vsync")) {
				Gdx.graphics.setVSync(getBoolean(key));
			} else if (key.equals("gfx.blur")) {
				Camera.blur = getBoolean(key);
			} else if (key.equals("gfx.blur.hd")) {
				Camera.blurHD = getBoolean(key);
			} else if (key.equals("gfx.multiblend")) {
				Camera.multiblend = getBoolean(key);
			} else if (key.equals("gfx.shadows")) {
				Camera.shadows = getBoolean(key);
			} else if (key.equals("gfx.shadows.check")) {
				Camera.shadowsCheck = getBoolean(key);
			} else if (key.equals("dev.show.ram")) {
				Camera.showRAM = getBoolean(key);
			} else if (key.equals("gfx.clear")) {
				Shadow.glclear = getBoolean(key);
			} else if (key.equals("gfx.blur.hq")) {
				if (getBoolean(key)) {
					Camera.blursize = 2f;
					Camera.blurrad = 4;
					Camera.blurdist = 1f/16f;
				} else {
					Camera.blursize = 3f;
					Camera.blurrad = 4;
					Camera.blurdist = 1f/16f;
				}
				if (Shadow.cam != null) {
					Shadow.resize();
				}
			} else if (key.equals("gfx.light.hd")) {
                if (getBoolean(key)) {
                    LightSystemHelper.lightFBFactor = 1f;
                    LightSystemHelper.lightFBSpeed = 1;
                } else {
                    LightSystemHelper.lightFBFactor = 0.5f;
                    LightSystemHelper.lightFBSpeed = 2;
                }
                if (Shadow.cam != null) {
                    Shadow.resize();
                }
            } else if (key.equals("gfx.light.blur")) {
                Camera.blurLight = getBoolean(key);
			} else if (key.equals("gfx.light.noclear")) {
				LightSystemHelper.lightFBClear = !getBoolean(key);
			} else if (key.equals("mp.user.name")) {
                if (Shadow.playerInfo != null) {
                    Shadow.playerInfo.setUserName(getString(key));
                }
            } else if (key.equals("mp.user.id")) {
                if (Shadow.playerInfo != null) {
                    Shadow.playerInfo.setUserID(getString(key));
                }
            } else if (key.equals("mp.user.session")) {
                if (Shadow.playerInfo != null) {
                    Shadow.playerInfo.setSessionID(getString(key));
                }
            }

			//TODO Add more entries
			//TODO Mod support
		}
	}

	//Setup

	public static void setup() {
		if (back != null) {
			throw new IllegalStateException("Options already set up!");
		}
		back = Gdx.app.getPreferences(Options.class.getName()+".settings");
		//TODO add more
		//TODO load from files...
		putBoolean("gfx.vsync", getBoolean("gfx.vsync", true));
		putBoolean("gfx.multiblend", getBoolean("gfx.multiblend", true));
		putBoolean("gfx.shadows", getBoolean("gfx.shadows", true));
		putBoolean("gfx.shadows.check", getBoolean("gfx.shadows.check", false));
		putBoolean("gfx.clear", getBoolean("gfx.clear", true));
		putBoolean("gfx.blur", getBoolean("gfx.blur", false));
		putBoolean("gfx.blur.hq", getBoolean("gfx.blur.hq", false));
		putBoolean("gfx.blur.hd", getBoolean("gfx.blur.hd", false));
		putBoolean("gfx.light.hd", getBoolean("gfx.light.hd", false));
		putBoolean("gfx.light.noglclear", getBoolean("gfx.light.noglclear", false));
		putBoolean("gfx.large", getBoolean("gfx.large", false));
		putBoolean("dev.show.ram", getBoolean("dev.show.ram", false));
		flush();
	}
}
