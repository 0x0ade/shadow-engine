package net.fourbytes.shadow;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import net.fourbytes.shadow.entities.Particle;
import net.fourbytes.shadow.utils.Cache;

public final class Garbage {
	private Garbage() {
	}

	//Caches

	public final static Cache<Rectangle> rects = new Cache(Rectangle.class);
	public final static Cache<Vector2> vec2s = new Cache(Vector2.class);

	//Utility / helper stuff
	public final static Array<Particle> particles = new Array<Particle>();

	public final static Json json = new Json();

}
