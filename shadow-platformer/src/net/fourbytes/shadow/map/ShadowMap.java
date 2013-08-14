package net.fourbytes.shadow.map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.LongMap;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Player;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * An ShadowMap is an specially saved map. It mostly differs from the TilED maps by saving an "snapshot" of 
 * the current state of the level into individual {@link Chunk}s instead of saving an "initial state" of 
 * the level into one general map. 
 */
public class ShadowMap {
	
	public LongMap<Chunk> chunks = new LongMap<Chunk>();
	
	public ShadowMap() {
	}
	
	/**
	 * Creates an fresh, "initial state" {@link GameObject}.
	 * @param level Level to create the {@link GameObject} in.
	 * @param x X position,
	 * @param y Y position
	 * @param ln Layer number
	 * @param tid Tile ID (optional, use 0 by default)
	 * @param type Type parameter ("block" or "entity") (optional)
	 * @param subtype Subtype parameter ("Player" or "BlockDissolve.1")
	 * @return {@link GameObject} "loaded" from map.
	 */
	public static GameObject convert(int x, int y, Layer layer, int tid, String type, String subtype) {
		GameObject obj = null;
		if (type == null || type.isEmpty()) {
			if (subtype.toLowerCase().startsWith("block")) {
				type = "block";
			} else {
				type = "entity";
			}
		}
		if ("block".equals(type)) {
			//System.out.println("tid: "+tid);
			Block block = BlockType.getInstance(subtype, x, y, layer);
			block.subtype = subtype;
			obj = block;
		} else if ("entity".equals(type)) {
			if ("Player".equals(subtype)) {
				Entity ent = new Player(new Vector2(x, y), layer);
				obj = ent;
			}
		} else {
			if (tid != 0) {
				throw new IllegalStateException("unknown type "+type+" for block id "+tid);
			}
		}
		return obj;
	}
	
	/**
	 * Creates an {@link MapObject} out of an {@link GameObject} . 
	 * @param go {@link GameObject} to convert
	 * @return {@link MapObject} that can be converted back 
	 * to create another {@link GameObject} <b> representing an SIMILAR (!)</b> {@link GameObject} to the original.
	 */
	public static MapObject convert(GameObject go) {
		MapObject mo = null;
		// TODO Auto-generated method stub
		return mo;
	}
	
	/**
	 * Creates an {@link GameObject} out of an {@link MapObject} . 
	 * @param mo {@link MapObject} to convert
	 * @param level Level to allocate the GameObject to.
	 * @return {@link GameObject} representing an closest-possible, 
	 * thru-stream-sendable replica of the original {@link GameObject}.
	 */
	public static GameObject convert(MapObject mo, Level level) {
		Layer layer = null;
		if (level != null) {
			layer = level.layers.get(mo.layer);
		}
		int tid = 0;
		GameObject go = convert((int) mo.x, (int)mo.y, layer, tid, mo.type, mo.subtype);
		
		//TODO Replace fields
		//TODO Replace fields in BlockType when TypeBlock
		
		return go;
	}
	
	/**
	 * Temporarily loads an ShadowMap into memory. Doesn't create an level.
	 * @param file File to load map from.
	 * @return ShadowMap containing {@link Chunk}s containing {@link MapObject}s NOT {@link GameObject}s.
	 */
	public static ShadowMap loadFile(FileHandle file) {
		ShadowMap map = null;
		//TODO Stub
		return map;
	}
	
	/**
	 * Converts the temporarily loaded ShadowMap to an level.
	 * @param level Level to fill.
	 */
	public void fillLevel(Level level) {
		//TODO Stub
	}
	
	/**
	 * Creates an ShadowMap from an level to save afterwards.
	 * @param level Level to get data from.
	 * @return ShadowMap containing {@link Chunk}s containing {@link MapObject}s converted from 
	 * {@link GameObject}s of the level.
	 */
	public static ShadowMap createFrom(Level level) {
		ShadowMap map = null;
		//TODO Stub
		return map;
	}
	
	/**
	 * Saves the ShadowMap to the file to load afterwards.
	 * @param file File to save the ShadowMap to.
	 */
	public void save(FileHandle file) {
		//TODO Stub
	}
	
}
