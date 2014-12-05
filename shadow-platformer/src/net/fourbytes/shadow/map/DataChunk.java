package net.fourbytes.shadow.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.Chunk;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.network.NetStream;

/**
 * A chunk contains an area of {@link GameObject}s beginning at the
 * given position with given size from a given map.
 * <br>
 * It is containing it's children as Data for saving space and
 * avoiding infinite recursion / bi-directional references when serializing.
 */
public class DataChunk extends Data implements Json.Serializable {
	
	public int x;
	public int y;
	
	public Array<MapObject> objects = new Array<MapObject>(MapObject.class);
	
	protected DataChunk() {
		this(0, 0);
	}
	
	protected DataChunk(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates and returns a chunk to send through {@link NetStream}s.
	 * @param x Initial x position
	 * @param y Initial y position
	 * @param level Level to get the chunk from
	 * @return Chunk containing all game objects in area of (x*size, y*size) to (x*size+size, y*size+size)
	 */
	public static DataChunk create(int x, int y, Level level) {
		return create(x, y, level, false);
	}

	/**
	 * Creates and returns a chunk to send through {@link NetStream}s.
	 * @param x Initial x position
	 * @param y Initial y position
	 * @param level Level to get the chunk from
	 * @param checkDirty only convert dirty chunks; return null otherwise.
	 * @return DataChunk containing all game objects in area of (x*size, y*size) to (x*size+size, y*size+size); null otherwise
	 */
	public static DataChunk create(int x, int y, Level level, boolean checkDirty) {
		Chunk lvlchunk = level.mainLayer.chunkmap.get(Coord.get(x, y));

		if (lvlchunk == null || (checkDirty && lvlchunk.dirty == 0)) {
			return null;
		}

		DataChunk chunk = new DataChunk(x, y);

		for (int i = 0; i < lvlchunk.entities.size; i++) {
			chunk.objects.add(ShadowMap.convert(lvlchunk.entities.items[i]));
		}

		for (int i = 0; i < lvlchunk.blocks.size; i++) {
			chunk.objects.add(ShadowMap.convert(lvlchunk.blocks.items[i]));
		}

		return chunk;
	}

	@Override
	public void write(Json json) {
		json.writeValue("x", x);
		json.writeValue("y", y);
		json.writeValue("objects", objects);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		x = jsonData.getInt("x", 0);
		y = jsonData.getInt("y", 0);
		JsonValue objects = jsonData.get("objects");
		if (objects != null && !objects.isNull()) {
			//iterate as the arrays may have different item-array types
			for (JsonValue current = objects.child; current != null; current = current.next) {
				this.objects.add(json.readValue(MapObject.class, current));
			}
		}
	}
	
}
