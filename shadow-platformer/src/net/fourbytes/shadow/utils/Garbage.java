package net.fourbytes.shadow.utils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.Json;
import net.fourbytes.shadow.entities.Particle;

public final class Garbage {
	private Garbage() {
	}

	//Caches

	public final static Cache<Rectangle> rects = new Cache(Rectangle.class);
	public final static Cache<Vector2> vec2s = new Cache(Vector2.class);

	//Utility / helper fields
	public final static Array<Particle> particles = new Array<Particle>();

	public final static Json json = new Json();

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

}
