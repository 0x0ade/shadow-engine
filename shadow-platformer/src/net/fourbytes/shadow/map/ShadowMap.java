package net.fourbytes.shadow.map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Mob;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.network.NetPlayer;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.utils.AsyncThread;
import net.fourbytes.shadow.utils.Garbage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * ShadowMap (SMF) is Shadow's own map format also used by the networking code or level generator.
 * </p>
 * <p>
 * It mostly differs from the TilED map format (TMX) by splitting it's contents into chunks, thus it's better
 * when loading big maps on the fly or multiplayer. Also, it's using JSON instead of XML, is compressed via
 * GZIP and avoids Base64 completely.
 * </p>
 * <p>
 * Unfortunately it isn't perfect yet. Current goals are to make it be more lightweight and faster than TMX (both
 * filesize and map loading overhead) but providing stability and no loss of information (while saving and in
 * multiplayer).
 * </p>
 * <p>
 * To reduce map saving overhead, every instance of ShadowMap optionally manages a self-destroying
 * AsyncThread inside the instance to operate on another thread, loosing the current thread from blocking calls.
 * </p>
 * <p>
 * Most importantly, ShadowMaps are storing every tile as individual object.
 * This allows setting parameters to tiles, but not in a manner of parameters that must be parsed by the loader but
 * directly as PJOs (Plain Java Objects), thus blending more with the source code of the objects than with the limits
 * of the map editor's parameter functions.
 * </p>
 * <p>
 * And yes, the SMF format is STILL missing a map editor.
 * </p>
 */
public class ShadowMap extends Data implements Json.Serializable {

    protected static AsyncThread thread;

	public long timestamp;
	public LongMap<DataChunk> chunkmap = new LongMap<DataChunk>();
	public LongArray chunksStored = new LongArray();
	public long chunkPlayer;
	public ObjectMap<String, Object> params = new ObjectMap<String, Object>();

	public FileHandle file;

	public ShadowMap() {
	}
	
	/**
	 * Creates a fresh, "initial state" {@link GameObject}.
	 * @param x X position
	 * @param y Y position
	 * @param layer The layer to create the GameObject in.
	 * @param tid Tile ID (optional, use 0 by default)
	 * @param type Type parameter ("block" or "entity") (optional)
	 * @param subtype Subtype parameter ("Player" or "BlockDissolve.1")
	 * @return {@link GameObject} "loaded" from map.
	 */
	public static GameObject convert(int x, int y, Layer layer, int tid, String type, String subtype) {
		/*
		if (Shadow.thread != Thread.currentThread()) {
			final GameObject[] go = {null};
			final int fx = x;
			final int fy = y;
			final Layer flayer = layer;
			final int ftid = tid;
			final String ftype = type;
			final String fsubtype = subtype;
			Gdx.app.postRunnable(new Runnable() {
				public void run() {
					go[0] = convert(fx, fy, flayer, ftid, ftype, fsubtype);
				}
			});
			while (go[0] == null) {
				try {
					Thread.sleep(10L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Thread.yield();
			}
			return go[0];
		}
		*/

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
        } else if ("particle".equals(type)) {
            Color color = null;
            if (subtype.contains(".")) {
                String[] split = subtype.split("\\.");
                subtype = split[0];
                color = Color.valueOf(split[1]);
            }
            obj = layer.level.systems.get(IParticleManager.class).create(subtype, new Vector2(x, y), layer, color, 0f, 0f);
        } else if ("entity".equals(type)) {
			if ("Player".equals(subtype)) {
				obj = new Player(new Vector2(x, y), layer);
			} else if ("NetPlayer".equals(subtype)) {
                obj = new NetPlayer(new Vector2(x, y), layer);
            } else if (subtype.startsWith("Mob")) {
				try {
					Class<? extends Mob> clazz = (Class<? extends Mob>)
							ClassReflection.forName("net.fourbytes.shadow."+subtype);
					Constructor<? extends Mob> constr = clazz.getConstructor(Vector2.class, Layer.class);
					constr.setAccessible(true);
					obj = constr.newInstance(new Vector2(x, y), layer);
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
        IsSaveable saveableGO = go.getClass().getAnnotation(IsSaveable.class);
        if (saveableGO != null && !saveableGO.value()) {
            return null;
        }

		MapObject mo = new MapObject();
		
        update(mo, go);
		
		return mo;
	}

    /**
     * Updates the given map object with data from the game object, clearing and thus overwriting old data.
     * @param mo MapObject to update
     * @param go GameObject from which the data to get from
     */
    public static void update(MapObject mo, GameObject go) {
        mo.id = go.getID();

        mo.x = go.pos.x;
        mo.y = go.pos.y;

        if (go instanceof Block) {
            mo.type = "block";
        } else if (go instanceof Particle) {
            mo.type = "particle";
            mo.subtype = go.layer.level.systems.get(IParticleManager.class).getName(((Particle)go).type);
            mo.subtype = mo.subtype + "." + go.baseColors[0];
            mo.args.put("rec", go.rec);
            mo.args.put("alpha", go.alpha);
            mo.args.put("light", go.light);
        } else if (go instanceof Entity) {
            mo.type = "entity";
        }

        if (go instanceof BlockType) {
            mo.subtype = ((Block)go).subtype;
        } else if (mo.subtype == null || mo.subtype.isEmpty()) {
            mo.subtype = go.getClass().getSimpleName();
        }

        mo.layer = go.layer.level.layers.findKey(go.layer, true, Integer.MAX_VALUE);

        mo.args.clear();

        Field[] fields = go.getClass().getFields();
        Field.setAccessible(fields, true);
        for (Field field : fields) {
            IsSaveable saveable = field.getAnnotation(IsSaveable.class);
            if (saveable != null && saveable.value()) {
                try {
                    mo.args.put(field.getName(), field.get(go));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	/**
	 * Creates a {@link GameObject} out of a {@link MapObject} .
	 * @param mo {@link MapObject} to convert
	 * @param level Level to allocate the GameObject to.
	 * @return {@link GameObject} representing a best-possible
	 * through-stream-send-able replica of the original {@link GameObject}.
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

        update(go, mo);
		
		return go;
	}

    /**
     * Updates the given game object with data from the map object, overwriting old data, leaving some data unchanged.
     * @param go GameObject to update
     * @param mo MapObject from which the data to get from
     */
    public static void update(GameObject go, MapObject mo) {
        go.setID(mo.id);

        go.pos.x = mo.x;
        go.pos.y = mo.y;

        if (go.layer != null && go instanceof Block) {
            go.layer.move((Block) go, Coord.get(go.pos.x, go.pos.y), Coord.get(mo.x, mo.y));
        }

        for (ObjectMap.Entry<String, Object> entry : mo.args.entries()) {
            try {
                Field field = go.getClass().getField(entry.key);
                field.setAccessible(true);
                field.set(go, entry.value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * Temporarily loads a ShadowMap into memory. Doesn't create a level.
	 * @param file File to load map from.
	 * @return ShadowMap containing {@link DataChunk}s containing {@link MapObject}s NOT {@link GameObject}s, or null when failed.
	 */
	public static ShadowMap loadFile(FileHandle file) {
		ShadowMap map = null;
		InputStream fis = null;
		GZIPInputStream gis = null;
		try {
			fis = file.read();
			gis = new GZIPInputStream(fis);
			map = Garbage.json.readValue(ShadowMap.class, null, Garbage.jsonReader.parse(gis));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (gis != null) {
				try {
					gis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	/**
	 * Converts the temporarily loaded ShadowMap to a level.
	 * @param level Level to fill.
	 */
	public void fillLevel(Level level) {
		fillLevel(level, null);
	}

	/**
	 * Converts the temporarily loaded ShadowMap to a level.
	 * @param level Level to fill.
	 * @param loading Optional LoadingLevel
	 */
	public void fillLevel(Level level, LoadingLevel loading) {
		if (loading != null) {
			loading.progress = 0;
			loading.progressMax = params.size + chunkmap.size;
		}

		Class<? extends Level> clazz = level.getClass();
		for (ObjectMap.Entry param : params.entries()) {
			try {
				Field field = clazz.getField((String) param.key);
				field.setAccessible(true);

				Class<?> fieldClazz = field.getType();

				Object obj = param.value;
				while (obj instanceof JsonValue) {
					obj = Garbage.json.readValue(fieldClazz, (JsonValue) obj);
				}

				if (obj instanceof Number) {
					Number num = (Number) obj;
					if (fieldClazz == int.class) {
						obj = num.intValue();
					} else if (fieldClazz == long.class) {
						obj = num.longValue();
					} else if (fieldClazz == float.class) {
						obj = num.floatValue();
					} else if (fieldClazz == double.class) {
						obj = num.doubleValue();
					} else if (fieldClazz == byte.class) {
						obj = num.byteValue();
					} else if (fieldClazz == short.class) {
						obj = num.shortValue();
					}
				} else if (obj instanceof Boolean) {
					obj = ((Boolean) obj).booleanValue();
				} else {
					obj = fieldClazz.cast(obj);
				}

				field.set(level, obj);
			} catch (Exception e) {
				System.err.println("Failed to bind property "+param.key+" to level!");
				e.printStackTrace();
			}
			if (loading != null) {
				loading.progress++;
			}
		}

		for (DataChunk chunk : chunkmap.values()) {
			convert(chunk, level, true);
			if (loading != null) {
				loading.progress++;
			}
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
	public Array<GameObject> convert(DataChunk chunk, Level level, boolean add) {
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
	 * Creates a new ShadowMap from a level to save afterwards.
	 * @param level Level to get data from.
	 * @return ShadowMap containing {@link DataChunk}s containing {@link MapObject}s converted from
	 * {@link GameObject}s of the level.
	 */
	public static ShadowMap createNewFrom(Level level) {
		return createNewFrom(level, null);
	}
	
	/**
	 * Creates a new ShadowMap from a level to save afterwards.
	 * @param level Level to get data from.
	 * @return ShadowMap containing {@link DataChunk}s containing {@link MapObject}s converted from
	 * {@link GameObject}s of the level.
	 * @param loading Optional LoadingLevel
	 */
	public static ShadowMap createNewFrom(Level level, LoadingLevel loading) {
        ShadowMap map = new ShadowMap();
        map.createFrom(level, loading);
        return map;
    }

    /**
     * Creates a ShadowMap from a level to save afterwards.
     * @param level Level to get data from.
     */
    public void createFrom(Level level) {
        createFrom(level, null);
    }

    /**
     * Creates a ShadowMap from a level to save afterwards.
     * @param level Level to get data from.
     * @param loading Optional LoadingLevel
     */
    public void createFrom(Level level, LoadingLevel loading) {
        clear();

		Field[] fields = level.getClass().getFields();
		Field.setAccessible(fields, true);

		if (loading != null) {
			loading.progress = 0;
			loading.progressMax = fields.length + level.mainLayer.entities.size + level.mainLayer.blocks.size;
		}

		for (Field field : fields) {
			IsSaveable saveable = field.getAnnotation(IsSaveable.class);
			if (saveable != null && saveable.value()) {
				try {
					params.put(field.getName(), field.get(level));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (loading != null) {
				loading.progress++;
			}
		}

		Layer layer = level.mainLayer;
		for (int i = 0; i < layer.entities.size; i++) {
			add0(layer.entities.items[i]);
			if (loading != null) {
				loading.progress++;
			}
		}
		for (int i = 0; i < layer.blocks.size; i++) {
			add0(layer.blocks.items[i]);
			if (loading != null) {
				loading.progress++;
			}
		}
	}
	
	protected void add0(GameObject go) {
		MapObject mo = convert(go);
        if (mo == null) {
            return;
        }
		long c = Coord.get((int)(mo.x / Chunk.size), (int)(mo.y / Chunk.size));
		DataChunk chunk = chunkmap.get(c);
		if (chunk == null) {
			chunk = new DataChunk();
			chunk.x = (int)(mo.x / Chunk.size);
			chunk.y = (int)(mo.y / Chunk.size);
			chunkmap.put(c, chunk);
		}
		if (go instanceof Player) {
			chunkPlayer = c;
		}
		chunk.objects.add(mo);
	}
	
	/**
	 * Saves the ShadowMap to the file to load afterwards.
	 * @param file File to save the ShadowMap to.
	 */
	public void save(FileHandle file) {
		OutputStream fos = null;
		GZIPOutputStream gos = null;
		OutputStreamWriter writer = null;
		JsonWriter writerJson = null;
		try {
			fos = file.write(false);
			gos = new GZIPOutputStream(fos);
			writer = new OutputStreamWriter(gos);
			writerJson = new JsonWriter(writer);
			writerJson.setOutputType(JsonWriter.OutputType.minimal);
			timestamp = System.currentTimeMillis();
			Garbage.json.toJson(this, ShadowMap.class, writerJson);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writerJson != null) {
				try {
					writerJson.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (gos != null) {
				try {
					gos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Updates the already existing ShadowMap from the file with additional content from this ShadowMap.
	 * @param file File of the ShadowMap to update.
	 * @param removeFromRAM Whether to try to remove the chunks from RAM or not.
	 */
	public void update(FileHandle file, boolean removeFromRAM) {
		if (!file.exists()) {
			save(file);
			return;
		}

		InputStream fis = null;
		GZIPInputStream gis = null;
		OutputStream fos = null;
		GZIPOutputStream gos = null;
		OutputStreamWriter writer = null;
		try {
			fis = file.read();
			gis = new GZIPInputStream(fis);

			JsonValue smf = Garbage.jsonReader.parse(gis);

			gis.close();

			fos = file.write(false);
			gos = new GZIPOutputStream(fos);
			writer = new OutputStreamWriter(gos);
			Json json = new Json();
			json.setWriter(writer);
			json.writeObjectStart();

			for (ObjectMap.Entry<String, Object> param : this.params.entries()) {
				if (param == null) {
					continue;
				}
				smf.remove("param."+param.key);
				json.writeValue("param."+param.key, param.value);
			}
			for (LongMap.Entry<DataChunk> chunk : chunkmap.entries()) {
				if (chunk == null) {
					continue;
				}
				smf.remove("chunk."+chunk.key);
				json.writeValue("chunk." + chunk.key, chunk.value);
				if (removeFromRAM) {
					chunksStored.add(chunk.key);
				}
			}
			if (removeFromRAM) {
				chunkmap.clear();
			}

			write0(json, smf);

			timestamp = System.currentTimeMillis();
			json.writeValue("timestamp", timestamp);
			json.writeValue("chunksStored", chunksStored);
			json.writeValue("chunkPlayer", chunkPlayer);

			json.writeObjectEnd();
		} catch (Throwable t) {
			t.printStackTrace();
			file.delete();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (gis != null) {
				try {
					gis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (gos != null) {
				try {
					gos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public AsyncThread asyncThread() {
		return asyncThread(true);
	}

	public AsyncThread asyncThread(boolean create) {
		if (create) {
			if (thread == null || thread.finished) {
				thread = new AsyncThread("ShadowMap AsyncThread " + (MathUtils.random(65536)), 10000L);
				thread.start();
			}
			thread.timeLast = System.currentTimeMillis();
		}
		return thread;
	}

	@Override
	public void write(Json json) {
		for (ObjectMap.Entry<String, Object> param : this.params.entries()) {
			if (param == null) {
				continue;
			}
			json.writeValue("param."+param.key, param.value);
		}
		for (LongMap.Entry<DataChunk> chunk : chunkmap.entries()) {
			if (chunk == null) {
				continue;
			}
			json.writeValue("chunk."+chunk.key, chunk.value);
		}
		json.writeValue("timestamp", timestamp);
		json.writeValue("chunksStored", chunksStored);
		json.writeValue("chunkPlayer", chunkPlayer);
	}

	protected void write0(Json json, JsonValue smfold) throws IOException {
		for (JsonValue current = smfold.child; current != null; current = current.next) {
			if (current.name.startsWith("chunk.") || current.name.startsWith("param.")) {
				//json.writeValue(current.name, );
				write1(json, current);
			}
		}
	}

	protected void write1(Json json, JsonValue jsonData) throws IOException {
		if (jsonData.name != null) {
			json.getWriter().name(jsonData.name);
		}

		if (!jsonData.isObject() && !jsonData.isArray()) {
			Object value = null;
			JsonValue.ValueType type = jsonData.type();
			if (type == JsonValue.ValueType.stringValue) {
				value = jsonData.asString();
			} else if (type == JsonValue.ValueType.doubleValue) {
				value = jsonData.asDouble();
			} else if (type == JsonValue.ValueType.longValue) {
				value = jsonData.asLong();
			} else if (type == JsonValue.ValueType.booleanValue) {
				value = jsonData.asBoolean();
			}
			json.getWriter().value(value);
			return;
		}

		if (jsonData.isArray()) {
			json.writeArrayStart();
		} else {
			json.writeObjectStart();
		}

		for (JsonValue current = jsonData.child; current != null; current = current.next) {
			write1(json, current);
		}

		if (jsonData.isArray()) {
			json.writeArrayEnd();
		} else {
			json.writeObjectEnd();
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		timestamp = jsonData.getLong("timestamp", 0);
		chunksStored.addAll(json.readValue("chunksStored", LongArray.class, jsonData));
		chunkPlayer = jsonData.getLong("chunkPlayer", 0);
		for (JsonValue current = jsonData.child; current != null; current = current.next) {
			if (current.name.startsWith("chunk.")) {
				this.chunkmap.put(Long.parseLong(current.name.substring(6)), json.readValue(DataChunk.class, current));
			}
			if (current.name.startsWith("param.")) {
				this.params.put(current.name.substring(6), json.readValue(null, current));
			}
		}
	}

    public void clear() {
        timestamp = 0L;
        chunkmap.clear();
        chunksStored.clear();
        chunkPlayer = 0L;
        params.clear();
    }
}
