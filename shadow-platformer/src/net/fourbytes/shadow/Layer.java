package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.utils.Cache;

/**
 * Placeholder for the layers in the levels. Processing happens in {@link Level}.
 * It is using long-coords given by the Coord class but most methods in this
 * class avoid using the Coords class itself to speed up performance (actually
 * to reduce overhead when calling external methods).
 */
public class Layer {

	protected final Cache<Array<Block>> cache = new Cache(Array.class, 32,
			new Object[] {false, 4, Block.class}, new Class[] {boolean.class, int.class, Class.class});

	public static enum BlockMapSystem {
		coordinate, //Default, most performance, most garbage
		row, //performance decreased, less garbage
		column, //performance decreased, less garbage
		none //least performance, "no garbage"
	}

	public static BlockMapSystem bms = BlockMapSystem.coordinate;
	public static int round = 1;
	protected IntMap<Array<Block>> rowmap = new IntMap<Array<Block>>(256);
	protected LongMap<Array<Block>> blockmap = new LongMap<Array<Block>>(1024);

	public Level level;
	public Array<GameObject> inView = new Array<GameObject>(false, 512, GameObject.class);

	public Array<Block> blocks = new Array<Block>(false, 4096, Block.class);
	public Array<Entity> entities = new Array<Entity>(false, 512, Entity.class);
	public Array<Particle> particles = new Array<Particle>(false, 512, Particle.class);

	public LongMap<Chunk> chunkmap = new LongMap<Chunk>(256);
	public Array<Chunk> chunks = new Array<Chunk>(false, 256, Chunk.class);

	public final Color tint = new Color(1f, 1f, 1f, 1f);

	public Layer(Level level) {
		this.level = level;
	}
	
	public void add(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.add(go);
		} else {
            level.goIDMap.put(go.getID(), go);
        }

		long cc = (long) ((int)(go.pos.x/Chunk.size)) << 32 | ((int)(go.pos.y/Chunk.size)) & 0xFFFFFFFFL;
		Chunk chunk = chunkmap.get(cc);
		if (chunk == null) {
			chunk = new Chunk((int)(go.pos.x/Chunk.size), (int)(go.pos.y/Chunk.size), this);
			chunkmap.put(cc, chunk);
			chunks.add(chunk);
		}
		go.chunk = chunk;

		//long c = Coord.get(go.pos.x/round, go.pos.y/round);
		long c = (long) ((int)(go.pos.x/round)) << 32 | ((int)(go.pos.y/round)) & 0xFFFFFFFFL;

		if (go instanceof Block) {
			blocks.add((Block) go);
			chunk.blocks.add((Block) go);
			Array<Block> al = get0(c);
			if (al == null) {
				al = put0(c);
			}
			al.add((Block) go);

			chunk.dirtify();
			if (!((Block) go).dynamic) {
				chunk.rerender = true;
			}
		} else if (go instanceof Particle) {
			particles.add((Particle) go);
			chunk.particles.add((Particle) go);
		} else if (go instanceof Entity) {
			entities.add((Entity) go);
			chunk.entities.add((Entity) go);
		}
	}
	
	public void remove(GameObject go) {
		if (this != level.mainLayer) {
			level.mainLayer.remove(go);
		} else {
            level.goIDMap.remove(go.getID());
        }

		long cc = (long) ((int)(go.pos.x/Chunk.size)) << 32 | ((int)(go.pos.y/Chunk.size)) & 0xFFFFFFFFL;
		Chunk chunk = chunkmap.get(cc);

		//long c = Coord.get(go.pos.x/round, go.pos.y/round);
		long c = (long) ((int)(go.pos.x/round)) << 32 | ((int)(go.pos.y/round)) & 0xFFFFFFFFL;

		inView.removeValue(go, true);
		if (go instanceof Block) {
			blocks.removeValue((Block) go, true);

			if (chunk != null) {
				chunk.blocks.removeValue((Block) go, true);
				chunk.dirtify();
				if (!((Block) go).dynamic) {
					chunk.rerender = true;
				}
			}

			Array<Block> al = get0(c);
			if (al != null) {
				al.removeValue((Block) go, true);
				if (al.size == 0) {
					remove0(c);
				}
			}
		} else if (go instanceof Particle) {
			if (this == level.mainLayer) {
				level.systems.get(IParticleManager.class).reset((Particle) go);
			}

			particles.removeValue((Particle) go, true);

			if (chunk != null) {
				chunk.particles.removeValue((Particle) go, true);
			}
		} else if (go instanceof Entity) {
			entities.removeValue((Entity) go, true);

			if (chunk != null) {
				chunk.entities.removeValue((Entity) go, true);
			}
		}

		go.chunk = null;

		if (chunk != null && chunk.blocks.size == 0 && chunk.particles.size == 0 && chunk.entities.size == 0) {
			chunkmap.remove(cc);
			chunks.removeValue(chunk, true);
		}
	}

	public void move(Block b, long oldc, long newc) {
		if (oldc == newc) {
			return;
		}

		if (this != level.mainLayer) {
			level.mainLayer.move(b, oldc, newc);
		}

		long oldcc = (long) (((int) (oldc >> 32)) / Chunk.size) << 32 | (((int) (oldc)) / Chunk.size) & 0xFFFFFFFFL;
		Chunk oldchunk = chunkmap.get(oldcc);
		if (oldchunk != null) {
			oldchunk.blocks.removeValue(b, true);
			if (!b.dynamic) {
				oldchunk.rerender = true;
			}
			if (oldchunk.blocks.size == 0 && oldchunk.particles.size == 0 && oldchunk.entities.size == 0) {
				chunkmap.remove(oldcc);
				chunks.removeValue(oldchunk, true);
			}
		}


		long newcc = (long) (((int) (newc >> 32)) / Chunk.size) << 32 | (((int) (newc)) / Chunk.size) & 0xFFFFFFFFL;
		Chunk newchunk = chunkmap.get(newcc);
		if (newchunk == null) {
			newchunk = new Chunk((((int) (newc >> 32)) / Chunk.size), (((int) (newc)) / Chunk.size), this);
			chunkmap.put(newcc, newchunk);
			chunks.add(newchunk);
		}
		if (newchunk != oldchunk) {
			newchunk.dirtify();
			if (!b.dynamic) {
				oldchunk.rerender = true;
			}
		}
		b.chunk = newchunk;

		//oldc = Coord.div(oldc, round));
		oldc = (long) (((int) (oldc >> 32)) / round) << 32 | (((int) (oldc)) / round) & 0xFFFFFFFFL;
		//newc = Coord.div(newc, round));
		newc = (long) (((int) (newc >> 32)) / round) << 32 | (((int) (newc)) / round) & 0xFFFFFFFFL;
		Array<Block> oal = get0(oldc);
		Array<Block> nal = get0(newc);
		if (oal == nal) {
			return;
		}
		if (nal == null) {
			nal = put0(newc);
		}
		if (oal != null) {
			oal.removeValue(b, true);

			if (oal.size == 0) {
				remove0(oldc);
			}
		}
		nal.add(b);
	}
	
	public Array<Block> get(long c) {
		//int cx = Coord.getX(c);
		int cx = (int) (c >> 32);
		//int cy = Coord.getY(c);
		int cy = (int) c;

		//c = Coord.div(c, round));
		c = (long) ((int)(cx/round)) << 32 | ((int)(cy/round)) & 0xFFFFFFFFL;

		Array<Block> vv = get0(c);
		Array<Block> v;
		if (round == 1 && bms == BlockMapSystem.coordinate) {
			v = vv;
		} else {
			v = cache.getNext();
			v.clear();
			if (vv != null) {
				for (int i = 0; i < vv.size; i++) {
					Block b = vv.items[i];
					if (cx == (int) b.pos.x && cy == (int) b.pos.y) {
						v.add(b);
					}
				}
			}
		}
		if (vv != null && vv.size == 0) {
			remove0(c);
		}
		return v;
	}
	
	protected Array<Block> get0(long c){
		if (bms == BlockMapSystem.coordinate) {
			return blockmap.get(c);
		} else if (bms == BlockMapSystem.row) {
			//rowmap.get(Coord.getY(c));
			return rowmap.get((int) c);
		} else if (bms == BlockMapSystem.column) {
			//rowmap.get(Coord.getX(c));
			return rowmap.get((int) (c >> 32));
		} else if (bms == BlockMapSystem.none) {
			return blocks;
		}
		return null;
	}
	
	protected Array<Block> put0(long c){
		Array<Block> al = null;
		if (bms == BlockMapSystem.coordinate) {
			al = new Array<Block>(false, 4, Block.class);
			blockmap.put(c, al);
		} else if (bms == BlockMapSystem.row) {
			al = new Array<Block>(false, 32, Block.class);
			//rowmap.put(Coord.getY(c), al);
			rowmap.put((int) c, al);
		} else if (bms == BlockMapSystem.column) {
			al = new Array<Block>(false, 32, Block.class);
			//rowmap.put(Coord.getX(c), al);
			rowmap.put((int) (c >> 32), al);
		}
		return al;
	}
	
	protected void remove0(long c){
		if (bms == BlockMapSystem.coordinate) {
			blockmap.remove(c);
		} else if (bms == BlockMapSystem.row) {
			//rowmap.remove(Coord.getY(c));
			rowmap.remove((int) c);
		} else if (bms == BlockMapSystem.column) {
			//rowmap.remove(Coord.getX(c));
			rowmap.remove((int) (c >> 32));
		}
	}
	
}
