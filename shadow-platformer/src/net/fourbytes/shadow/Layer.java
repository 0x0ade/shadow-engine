package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import net.fourbytes.shadow.utils.Cache;

/**
 *	Placeholder for the layers in the levels. Processing happens in {@link Level}.
 */
public class Layer {

	public static class Chunk {
		public Layer layer;
		public long c;

		public Array<Block> blocks = new Array<Block>(false, 4096);

		protected IntMap<Array<Block>> rowmap = new IntMap<Array<Block>>(256);
		protected LongMap<Array<Block>> blockmap = new LongMap<Array<Block>>(1024);

		public Chunk(Layer layer, long c) {
			this.layer = layer;
			this.c = c;
		}
	}

	protected final static Cache<Array> cache = new Cache(Array.class, 16,
			new Object[] {false, 4}, new Class[] {boolean.class, int.class});

	public static enum BlockMapSystem {
		coordinate, //Default, most performance, most garbage
		row, //performance decreased, less garbage
		column, //performance decreased, less garbage
		none //least performance, "no garbage"
	}
	public static BlockMapSystem bms = BlockMapSystem.coordinate;
	protected static BlockMapSystem lastbms;
	public static int round = 2;

	public Level level;
	public Array<GameObject> inView = new Array<GameObject>(false, 512);

	public Array<Block> blocks = new Array<Block>(false, 4096);
	public Array<Entity> entities = new Array<Entity>(false, 512);

	public LongMap<Chunk> chunkmap = new LongMap<Chunk>(1024);
	public static int chunksize = 18;

	public final Color tint = new Color(1f, 1f, 1f, 1f);

	public Layer(Level level) {
		this.level = level;
	}
	
	public void add(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.add(go);
		}
		if (go instanceof Block) {
			blocks.add((Block) go);
			Array<Block> al = get0(Coord.get(go.pos.x/round, go.pos.y/round));
			if (al == null) {
				al = put0(Coord.get(go.pos.x/round, go.pos.y/round));
			}
			al.add((Block) go);
			chunkmap.get(Coord.div(Coord.get(go.pos.x/round, go.pos.y/round), chunksize)).blocks
					.add((Block) go);
		} else if (go instanceof Entity) {
			entities.add((Entity) go);
		}
	}
	
	public void remove(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.remove(go);
		}
		inView.removeValue(go, true);
		if (go instanceof Block) {
			blocks.removeValue((Block) go, true);
			Array al = get0(Coord.get(go.pos.x/round, go.pos.y/round));
			if (al != null) {
				al.removeValue((Block) go, true);
				chunkmap.get(Coord.div(Coord.get(go.pos.x/round, go.pos.y/round), chunksize)).blocks
						.removeValue((Block) go, true);
				if (al.size == 0) {
					remove0(Coord.get(go.pos.x/round, go.pos.y/round));
				}
			}
		} else if (go instanceof Entity) {
			entities.removeValue((Entity) go, true);
		}
	}

	public void move(Block b, long oldc, long newc) {
		if (this != level.mainLayer) {
			level.mainLayer.move(b, oldc, newc);
		}
		int oldx = Coord.getX(oldc);
		int oldy = Coord.getY(oldc);
		int newx = Coord.getX(newc);
		int newy = Coord.getY(newc);
		Array oal = get0(Coord.get(oldx/round, oldy/round));
		Array nal = get0(Coord.get(newx/round, newy/round));
		if (oal == nal) {
			return;
		}
		if (nal == null) {
			nal = put0(Coord.get(newx/round, newy/round));
		}
		if (oal != null) {
			oal.removeValue(b, true);
			chunkmap.get(Coord.div(Coord.get(oldx/round, oldy/round), chunksize)).blocks
					.removeValue(b, true);

			if (oal.size == 0) {
				remove0(Coord.get(oldx/round, oldy/round));
			}
		}
		nal.add(b);
		chunkmap.get(Coord.div(Coord.get(newx/round, newy/round), chunksize)).blocks
				.add(b);
	}
	
	public Array<Block> get(long c) {
		int cx = Coord.getX(c);
		int cy = Coord.getY(c);
		Array<Block> vv = get0(Coord.get(cx/round, cy/round));
		/*
		if (v == null) {
			v = new Array<Block>(true, 32);
			blockmap.put(c, v);
		}
		*/
		Array<Block> v = cache.getNext();
		v.clear();
		if (vv != null) {
			for (Block b : vv) {
				if (cx == (int)b.pos.x && cy == (int)b.pos.y) {
					v.add(b);
				}
			}
		}
		if (vv != null && vv.size == 0) {
			remove0(Coord.get(cx/round, cy/round));
		}
		return v;
	}
	
	protected Array<Block> get0(long c){
		updateSystem0();

		long cc = Coord.div(c, chunksize);
		Chunk chunk = chunkmap.get(cc);
		if (chunk == null) {
			return null;
		}

		Array<Block> al = null;
		switch (bms) {
		case coordinate:
			al = chunk.blockmap.get(c);
			break;
		case row:
			al = chunk.rowmap.get(Coord.getY(c));
			break;
		case column:
			al = chunk.rowmap.get(Coord.getX(c));
			break;
		case none:
			al = blocks;
			break;
		}
		return al;
	}
	
	protected Array<Block> put0(long c){
		updateSystem0();

		long cc = Coord.div(c, chunksize);
		Chunk chunk = chunkmap.get(cc);
		if (chunk == null) {
			chunk = new Chunk(this, cc);
			chunkmap.put(cc, chunk);
		}

		Array<Block> al = null;
		switch (bms) {
		case coordinate:
			al = new Array<Block>(false, 4);
			chunk.blockmap.put(c, al);
			break;
		case row:
			al = new Array<Block>(false, 32);
			chunk.rowmap.put(Coord.getY(c), al);
			break;
		case column:
			al = new Array<Block>(false, 32);
			chunk.rowmap.put(Coord.getX(c), al);
			break;
		case none:
			break;
		}
		return al;
	}
	
	protected void remove0(long c){
		updateSystem0();

		long cc = Coord.div(c, chunksize);
		Chunk chunk = chunkmap.get(cc);
		if (chunk == null) {
			return;
		}
		
		switch (bms) {
		case coordinate:
			chunk.blockmap.remove(c);
			break;
		case row:
			chunk.rowmap.remove(Coord.getY(c));
			break;
		case column:
			chunk.rowmap.remove(Coord.getX(c));
			break;
		case none:
			break;
		}
	}
	
	protected void updateSystem0() {
		if (lastbms != null && lastbms != bms) {
			//TODO change the system properly instead of throwing error
			throw new Error("Change of the blockmap system while in-level not supported!");
		}
	}
	
}
