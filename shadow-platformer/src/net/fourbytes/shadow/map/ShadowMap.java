package net.fourbytes.shadow.map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Mob;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.utils.Garbage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * ShadowMap (SMF) is Shadow's own / native map format.
 * </p>
 * <p>
 * It mostly differs from the TilED map format (TMX) by splitting it's contents into chunks, thus it's perfect
 * when loading big maps on the fly or multiplayer. Also, it's using JSON instead of XML, is compressed via
 * GZIP and avoids Base64 completely.
 * </p>
 * <p>
 * Unfortunately it isn't perfect yet. Current goals are to make it be more lightweight and faster than TMX (both
 * filesize and map loading overhead) but providing stability and no loss of information (while saving and in
 * multiplayer).
 * </p>
 * <p>
 * Still, one of it's main points against TMX is already available: Storing every tile as individual object.
 * This allows setting parameters to tiles, but not in a manner of parameters that must be parsed by the loader but
 * directly as PJOs (Plain Java Objects), thus blending more with the source code of the objects than with the limits
 * of the map editor's parameter functions.
 * </p>
 * <p>
 * And yes, the SMF format is STILL missing a map editor.
 * </p>
 */
public class ShadowMap {

	public LongMap<Chunk> chunks = new LongMap<Chunk>();
	public long playerchunk;

	public ObjectMap<String, Object> params = new ObjectMap<String, Object>();
	
	public ShadowMap() {
	}
	
	/**
	 * Creates a fresh, "initial state" {@link GameObject}.
	 * @param x X position,
	 * @param y Y position
	 * @param layer The layer to create the GameObject in.
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
			if (block.getTexture(block.imgIDs[0]) != null) {
				obj = block;
			}
		} else if ("entity".equals(type)) {
			if ("Player".equals(subtype)) {
				Entity ent = new Player(new Vector2(x, y), layer);
				obj = ent;
			} else if (subtype.startsWith("Mob")) {
				try {
					Class clazz = ShadowMap.class.getClassLoader().loadClass("net.fourbytes.shadow."+subtype);
					Mob mob = (Mob) clazz.getConstructor(Vector2.class, Layer.class).newInstance(new Vector2(x, y), layer);
					obj = mob;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			if (tid != 0) {
				throw new IllegalStateException("unknown type "+type+" for block id "+tid);
			}
		}
		return obj;
	}
	
	/**
	 * Creates a {@link MapObject} out of a {@link GameObject}.
	 * @param go {@link GameObject} to convert
	 * @return {@link MapObject} that can be converted back 
	 * to create another {@link GameObject} <b> representing a SIMILAR (!)</b> {@link GameObject} to the original.
	 */
	public static MapObject convert(GameObject go) {
		MapObject mo = new MapObject();
		
		mo.x = go.pos.x;
		mo.y = go.pos.y;
		
		if (go instanceof Block) {
			mo.type = "block";
		} else if (go instanceof Entity) {
			mo.type = "entity";
		}
		
		if (go instanceof TypeBlock) {
			mo.subtype = ((Block)go).subtype;
		} else if (mo.subtype == null || mo.subtype.isEmpty()) {
			mo.subtype = go.getClass().getSimpleName();
		}
		
		for (IntMap.Entry<Layer> entry : go.layer.level.layers.entries()) {
			if (entry.value == go.layer) {
				mo.layer = entry.key;
				break;
			}
		}
		
		Object o = go;
		if (go instanceof TypeBlock) {
			o = ((TypeBlock)go).type;
		}
		
		Field[] fields = o.getClass().getFields();
		for (Field field : fields) {
			Saveable saveable = field.getAnnotation(Saveable.class);
			if (saveable != null) {
				try {
					mo.args.put(field.getName(), field.get(o));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return mo;
	}
	
	/**
	 * Creates a {@link GameObject} out of a {@link MapObject} .
	 * @param mo {@link MapObject} to convert
	 * @param level Level to allocate the GameObject to.
	 * @return {@link GameObject} representing a best-possible
	 * thru-stream-sendable replica of the original {@link GameObject}.
	 */
	public static GameObject convert(MapObject mo, Level level) {
		Layer layer = null;
		if (level != null) {
			layer = level.layers.get(mo.layer);
			if (layer == null) {
				level.fillLayer(mo.layer);
				layer = level.layers.get(mo.layer);
			}
		}
		int tid = 0;
		GameObject go = convert((int)mo.x, (int)mo.y, layer, tid, mo.type, mo.subtype);
		if (go == null) {
			return null;
		}
		go.pos.x = mo.x;
		go.pos.y = mo.y;
		
		Object o = go;
		if (o instanceof TypeBlock) {
			o = ((TypeBlock)o).type;
		}
		
		for (ObjectMap.Entry<String, Object> entry : mo.args.entries()) {
			try {
				o.getClass().getField(entry.key).set(o, entry.value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return go;
	}
	
	/**
	 * Temporarily loads a ShadowMap into memory. Doesn't create a level.
	 * @param file File to load map from.
	 * @return ShadowMap containing {@link Chunk}s containing {@link MapObject}s NOT {@link GameObject}s, or null when failed.
	 */
	public static ShadowMap loadFile(FileHandle file) {
		ShadowMap map = null;
		try {
			InputStream fis = file.read();
			GZIPInputStream gis = new GZIPInputStream(fis);
			map = Garbage.json.fromJson(ShadowMap.class, gis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * Converts the temporarily loaded ShadowMap to a level.
	 * @param level Level to fill.
	 */
	public void fillLevel(Level level) {
		Class<? extends Level> clazz = level.getClass();
		for (ObjectMap.Entry param : params.entries()) {
			try {
				Field field = clazz.getField((String)param.key);
				field.set(level, param.value);
			} catch (Exception e) {
				System.err.println("Failed to bind property "+param.key+" to level!");
				e.printStackTrace();
			}
		}

		for (Chunk chunk : chunks.values()) {
			convert(chunk, level, true);
		}
	}

	protected Array<GameObject> gos = new Array<GameObject>();
	/**
	 * Converts the content of temporarily loaded chunk, binding it (<b>NOT</b> adding it when add == false) to a level.
	 * @param chunk Chunk to convert.
	 * @param level Level to fill.
	 * @param add Add the result of conversion to level?
	 * @return Result of conversion. Note: The returned {@link Array} will be reused for further converts!
	 */
	public Array<GameObject> convert(Chunk chunk, Level level, boolean add) {
		gos.clear();
		for (MapObject mo : chunk.objects) {
			GameObject go = convert(mo, level);
			if (go != null && add) {
				go.layer.add(go);
				if (level != null) {
					if (go instanceof Player) {
						level.player = (Player) go;
					}
				}
			}
			gos.add(go);
		}
		/*
		level.ftick = true;
		level.tickid = 0;
		level.tick();
		level.ftick = true;
		level.tickid = 0;
		*/
		return gos;
	}
	
	/**
	 * Creates a ShadowMap from a level to save afterwards.
	 * @param level Level to get data from.
	 * @return ShadowMap containing {@link Chunk}s containing {@link MapObject}s converted from 
	 * {@link GameObject}s of the level.
	 */
	public static ShadowMap createFrom(Level level) {
		ShadowMap map = new ShadowMap();

		Field[] fields = level.getClass().getFields();
		for (Field field : fields) {
			Saveable saveable = field.getAnnotation(Saveable.class);
			if (saveable != null) {
				try {
					map.params.put(field.getName(), field.get(level));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (Layer layer: level.layers.values()) {
			for (GameObject go : layer.blocks) {
				add0(map, go);
			}
			for (GameObject go : layer.entities) {
				add0(map, go);
			}
		}
		return map;
	}
	
	protected static void add0(ShadowMap map, GameObject go) {
		MapObject mo = convert(go);
		long c = Coord.get((int)(mo.x / Chunk.size), (int)(mo.y / Chunk.size));
		Chunk chunk = map.chunks.get(c);
		if (chunk == null) {
			chunk = new Chunk();
			chunk.x = (int)(mo.x / Chunk.size);
			chunk.y = (int)(mo.y / Chunk.size);
			map.chunks.put(c, chunk);
		}
		if (go instanceof Player) {
			map.playerchunk = c;
		}
		chunk.objects.add(mo);
	}
	
	/**
	 * Saves the ShadowMap to the file to load afterwards.
	 * @param file File to save the ShadowMap to.
	 */
	public void save(FileHandle file) {
		try {
			String json = Garbage.json.toJson(this);
			OutputStream fos = file.write(false);
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			gos.write(json.getBytes("UTF-8"));
			gos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
