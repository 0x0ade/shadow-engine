package net.fourbytes.shadow;

import java.util.ArrayList;
import java.util.Vector;

import net.fourbytes.shadow.Input.KeyListener;
import net.fourbytes.shadow.blocks.BlockLogic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public final class Garbage {
	private Garbage() {
	}
	
	public final static Array<Particle> particles = new Array<Particle>();
	public final static Rectangle genrec = new Rectangle();
	public final static Rectangle rect = new Rectangle();
	public final static Rectangle recta = new Rectangle();
	public final static Rectangle rectb = new Rectangle();
	public final static Rectangle rectc = new Rectangle();
	public final static Rectangle rectd = new Rectangle();
	public final static Rectangle recte = new Rectangle();
	public final static Rectangle rectf = new Rectangle();
	/*
	public final static Coord coord = new Coord(0, 0);
	public final static Coord coorda = new Coord(0, 0);
	public final static Coord coordb = new Coord(0, 0);
	public final static Coord coordc = new Coord(0, 0);
	public final static Coord coordd = new Coord(0, 0);
	public final static Coord coorde = new Coord(0, 0);
	public final static Coord coordf = new Coord(0, 0);
	*/
	public final static Vector2 vec2 = new Vector2();
	public final static Vector2 vec2a = new Vector2();
	public final static Vector2 vec2b = new Vector2();
	public final static Vector2 vec2c = new Vector2();
	public final static Vector2 vec2d = new Vector2();
	public final static Vector2 vec2e = new Vector2();
	public final static Vector2 vec2f = new Vector2();
	
	public final static Json json = new Json();
	
}
