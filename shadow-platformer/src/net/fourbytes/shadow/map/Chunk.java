package net.fourbytes.shadow.map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.network.Data;

/**
 *	A chunk contains an area of {@link GameObject}s beginning at the given position with given size from a given map. 
 * It's containing it's children as Data for saving space and avoiding infinite recursion / bi-directional references when serializing. 
 */
public class Chunk extends Data {
	
	public static float size = 15;
	
	public int x;
	public int y;
	
	public Array<MapObject> objects = new Array<MapObject>();
	
	protected Chunk() {
		this(0, 0);
	}
	
	protected Chunk(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public final static Rectangle tmpcb = new Rectangle();
	public final static Rectangle tmpeb = new Rectangle();
	
	/**
	 * Creates and returns an chunk to send thru {@link Stream}s.
	 * @param x Initial x position
	 * @param y Initial y position
	 * @param level Level to get the chunk from
	 * @return Chunk containing all game objects in area of (x, y) to (x+size, y+size)
	 */
	public static Chunk create(int x, int y, Level level) {
		Chunk chunk = new Chunk(x, y);
		tmpcb.set(x, y, size, size);
		
		for (Entity e : level.mainLayer.entities) {
			tmpeb.set(e.pos.x, e.pos.y, e.rec.width, e.rec.height);
			if (tmpeb.overlaps(tmpcb)) {
				chunk.objects.add(ShadowMap.convert(e));
			}
		}
		
		for (int xx = x; xx <= x+size; xx++) {
			for (int yy = y; yy <= y+size; yy++) {
				Array<Block> blocks = level.mainLayer.get(Coord.get(xx, yy));
				if (blocks != null) {
					for (Block b : blocks) {
						tmpeb.set(b.pos.x, b.pos.y, b.rec.width, b.rec.height);
						if (tmpeb.overlaps(tmpcb)) {
							chunk.objects.add(ShadowMap.convert(b));
						}
					}
				}
			}
		}
		
		return chunk;
	}
	
}
