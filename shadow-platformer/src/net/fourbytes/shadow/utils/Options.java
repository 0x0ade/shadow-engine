package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

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
			}

			//TODO Add more entries
		}
	}

	//Setup

	public static void setup() {
		if (back != null) {
			throw new IllegalStateException("Options already set up!");
		}
		back = Gdx.app.getPreferences(Options.class.getName()+".settings");
		putBoolean("gfx.blur", getBoolean("gfx.blur", false));
		putBoolean("gfx.blur.twice", getBoolean("gfx.blur.twice", false));
		flush();
	}
}
