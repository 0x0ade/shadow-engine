package net.fourbytes.shadow.stream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.TypeBlock;

/**
 * GameObjectData contains all needed {@link Data} to send {@link GameObject}s thru {@link IStream}s. <br>
 * It is only a parent class containing an general {@link #pack(GameObject)} method linking to the correspondent subclasses.
 */
public abstract class GameObjectData<T extends GameObject> extends Data {
	
	public String clazz;
	
	public float alpha = 1f;
	public int layer;
	public Vector2 pos = new Vector2(0, 0);
	public Rectangle rec = new Rectangle(0, 0, 0, 0);
	public Rectangle renderoffs = new Rectangle(0, 0, 0, 0);
	public boolean solid = true;
	public Color light = new Color(1f, 1f, 1f, 0f);
	public Color lightTint = new Color(1f, 1f, 1f, 1f);
	public boolean passSunlight = false;
	public Color tintSunlight = new Color(1f, 1f, 1f, 1f);
	public Color tintDarklight = new Color(0f, 0f, 0f, 1f);
	public float highlighted = 0f;
	
	public GameObjectData() {
		super();
	}
	
	/**
	 * Converts an {@link GameObject} to it's correspondent {@link GameObjectData} (subclass). <br>
	 * This is the general method linking to the subclasses maintaining the given type.
	 * @param go {@link GameObject} to convert
	 * @return {@link Data} to send thru streams.
	 */
	public final static <V extends GameObject> GameObjectData<V> autopack(V go) {
		GameObjectData<V> god = null;
		
		if (go instanceof Entity) {
			god = (GameObjectData<V>) new EntityData();
		}
		if (go instanceof Block) {
			if (go instanceof TypeBlock) {
				god = (GameObjectData<V>) new TypeBlockData();
			} else {
				god = (GameObjectData<V>) new BlockData();
			}
		}
		
		if (god == null) { //Well... it will never happen, right?
			System.err.println("No GameObjectData for GameObject "+go);
		} else {
			god.pack(go);
		}
		
		return god;
	}
	
	/**
	 * Converts an {@link GameObject} to it's correspondent {@link GameObjectData}. <br>
	 * Override {@link #pack0(GameObject)} instead of this.
	 * @param go {@link GameObject} to convert
	 * @return {@link Data} to send thru streams.
	 */
	public final void pack(T go){
		clazz = go.getClass().getName();
		
		alpha = go.alpha;
		layer = 0; //TODO Get layer
		pos.set(go.pos);
		rec.set(go.rec);
		renderoffs.set(go.renderoffs);
		solid = go.solid;
		light.set(go.light);
		lightTint.set(go.lightTint);
		passSunlight = go.passSunlight;
		tintSunlight.set(go.tintSunlight);
		tintDarklight.set(go.tintDarklight);
		highlighted = go.highlighted;
		
		pack0(go);
	}
	
	/**
	 * Converts an {@link GameObject} to it's correspondent {@link GameObjectData}. <br>
	 * Override this instead of {@link #pack(GameObject)}.
	 * @param go {@link GameObject} to convert
	 * @return {@link Data} to send thru streams.
	 */
	public abstract void pack0(T go);
	
	/**
	 * Converts this {@link GameObjectData} back to it's correspondent {@link GameObject}.
	 * @return {@link GameObject} packed in this {@link GameObjectData}.
	 */
	public abstract GameObject unpack();
	
}
