package net.fourbytes.shadow.stream;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.GameObject;

/**
 * GameObjectData contains all needed {@link Data} to send {@link GameObject}s thru {@link IStream}s.
 */
public class GameObjectData extends Data {
	
	public GameObjectData() {
	}
	
	/**
	 * Converts an {@link GameObject} to it's {@link GameObjectData}.
	 * @param go {@link GameObject} to convert
	 * @return {@link Data} to send thru streams.
	 */
	public static GameObjectData pack(GameObject go) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
