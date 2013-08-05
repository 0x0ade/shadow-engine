package net.fourbytes.shadow.stream;

import net.fourbytes.shadow.Level;

/**
 *	A chunk contains an area of {@link GameObject}s beginning at the given position with given size from a given map. 
 * It's containing it's children as Data for saving space and avoiding infinite recursion / bi-directional references when serializing. 
 */
public class Chunk {
	
	public static final int size = 15;
	
	public int x;
	public int y;
	
	protected Chunk() {
		this(0, 0);
	}
	
	protected Chunk(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Chunk create(int x, int y, Level level) {
		Chunk chunk = new Chunk(x, y);
		//TODO get GameObjects at given region
		//TODO add GameObjects to chunk
		return chunk;
	}
	
}
