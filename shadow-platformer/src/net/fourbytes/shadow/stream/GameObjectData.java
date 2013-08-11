package net.fourbytes.shadow.stream;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.TypeBlock;

/**
 * GameObjectData contains all needed {@link Data} to send {@link GameObject}s thru {@link IStream}s. <br>
 * It is only a parent class containing an general {@link #pack(GameObject)} method linking to the correspondent subclasses.
 */
public abstract class GameObjectData<T extends GameObject> extends Data {
	
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
		
		if (god != null) { //Well... it will never happen, right?
			god.pack(go);
		}
		
		return god;
	}
	
	/**
	 * Converts an {@link GameObject} to it's correspondent {@link GameObjectData}.
	 * @param go {@link GameObject} to convert
	 * @return {@link Data} to send thru streams.
	 */
	public abstract GameObjectData<T> pack(T go);
	
	/**
	 * Converts this {@link GameObjectData} back to it's correspondent {@link GameObject}.
	 * @return {@link GameObject} packed in this {@link GameObjectData}.
	 */
	public abstract GameObject unpack();
	
}
