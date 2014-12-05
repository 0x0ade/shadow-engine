package net.fourbytes.shadow.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import net.fourbytes.shadow.Particle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Garbage {
	private Garbage() {
	}

	//Caches

	public final static Cache<Rectangle> rects = new Cache<Rectangle>(Rectangle.class);
	public final static Cache<Vector2> vec2s = new Cache<Vector2>(Vector2.class);
	public final static Cache<Color> colors = new Cache<Color>(Color.class);
	public final static Cache<MultiObject> multiobjs = new Cache<MultiObject>(MultiObject.class, 64);

	//Utility / helper fields
	public final static Array<Particle> particles = new Array<Particle>(Particle.class);

	public final static IntMap<String> mapIntString = new IntMap<String>();

	public final static Matrix4 matrix1x1 = new OrthographicCamera(1, 1).combined;

	public final static Json json = new Json();
	public final static JsonReader jsonReader = new JsonReader();
	public final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

	//Utility / helper methods

	public final static int getPreviousKey(IntIntMap map, int start, int alt) {
		return getNextKey(map, start, alt, -1);
	}

	public final static int getNextKey(IntIntMap map, int start, int alt) {
		return getNextKey(map, start, alt, 1);
	}

	private final static int getNextKey(IntIntMap map, int start, int alt, int dir) {
		for (int i = start; (dir>0)?(i < alt):(i > alt); i += dir) {
			if (map.containsKey(i)) {
				return i;
			}
		}
		return alt;
	}

	public static String dateCurrent() {
		return Garbage.dateFormat.format(Calendar.getInstance().getTime());
	}

	public static String getStringForInt(int i) {
		String str = mapIntString.get(i);
		if (str != null) {
			return str;
		}
		str = Integer.toString(i);
		mapIntString.put(i, str);
		return str;
	}

	public static JsonValue get(JsonValue value, String name) {
		JsonValue current = value.child;
		while (current != null && !name.equalsIgnoreCase(current.name)) {
			current = current.next;
		}
		return current;
	}
}
