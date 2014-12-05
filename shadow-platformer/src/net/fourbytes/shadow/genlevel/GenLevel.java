package net.fourbytes.shadow.genlevel;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.LongArray;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.DataChunk;
import net.fourbytes.shadow.map.IsSaveable;
import net.fourbytes.shadow.map.MapObject;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.mod.ModManager;
import net.fourbytes.shadow.utils.AsyncThread;
import net.fourbytes.shadow.utils.gdx.LongIntMap;

import java.lang.reflect.Field;
import java.util.Random;

public class GenLevel extends Level {

	@IsSaveable
	public int seed = (int) (Math.random()*(Integer.MAX_VALUE/2));
	/*
	 * Seed list:
	 * 515421 
	 * 65525 
	 * -229985452 generates hello 
	 * -147909648 generates world 
	 * 
	 */
	public Random rand = new Random(seed);

	@IsSaveable
	public boolean storeAuto = true;
	protected boolean storeAutoDelayed = false;
	public boolean storeForce = false;

	//ShadowMap actually stores generated chunks. They just need to be re-added later on...
	//TODO add chunks to this list after loaded from file
	public LongArray generated = new LongArray();
	//ShadowMap actually stores that
	public LongArray stored = new LongArray();
	//only temporary, so no Saveable annotation
	public LongIntMap ageChunks = new LongIntMap();
	//only temporary, so no Saveable annotation
	public int ageCurrent = 0;
	@IsSaveable
	public int ageOffset = 60*5;//TODO Change for production builds!
	//Seeded semi-randoms / maths should "save" this.
	public IntIntMap xHeight = new IntIntMap();
	//Seeded semi-randoms / maths should "save" this.
	public IntIntMap xStone = new IntIntMap();

	//TODO saveable?
	public CaveGen cavegen = new DefaultCaveGen(this);

	public ShadowMap mapLoaded;

	public GenLevel() {
		map = new ShadowMap();
		map.file = Shadow.getDir("saves").child("tmpgen.smf");

		fillLayer(0);
		fillLayer(1);

		float d = 0.5f;
		layers.get(0).tint.set(d, d, d, 1f);

		//generateChunks(-2*Chunk.size, 2*Chunk.size, 2*Chunk.size, 2*Chunk.size);

		Player p = new Player(new Vector2(0f, -5f), layers.get(1));
		layers.get(1).add(p);
		player = p;
		
		c = new Cursor(new Vector2(0f, 0f), layers.get(1));

		System.gc();

		dirtify = true;
		ready = true;
	}
	
	public boolean generateChunks(int fromx, int tox, int fromy, int toy) {
		fromx = (fromx/Chunk.size)*Chunk.size;
		tox = (tox/Chunk.size)*Chunk.size;
		fromy = (fromy/Chunk.size)*Chunk.size;
		toy = (toy/Chunk.size)*Chunk.size;
		boolean generated = false;

		for (int xx = fromx; xx < tox; xx+=Chunk.size) {
			for (int yy = fromy; yy < toy; yy+=Chunk.size) {
				if (generateChunk(xx, yy)) {
					generated = true;
				}
			}
		}

		return generated;
	}
	
	public boolean generateChunk(int xx, int yy) {
		xx = (xx/Chunk.size)*Chunk.size;
		yy = (yy/Chunk.size)*Chunk.size;

		//Check whether current chunk already generated
		long chunkc = Coord.get(xx / Chunk.size, yy / Chunk.size);
		ageChunks.put(chunkc, ageCurrent);
		if (stored.contains(chunkc)) {
			loadChunk(chunkc);
			return false;
		}
		if (generated.contains(chunkc)) {
			return false;
		} else {
			generated.add(chunkc);
		}

		dirtify = false;

		//"clean" the chunk (could be dirty because of f.e. water off-screen)
		Chunk chunk;

		chunk = mainLayer.chunkmap.get(chunkc);
		if (chunk != null) {
			chunk.dirty = 0;
		}

		chunk = layers.get(1).chunkmap.get(chunkc);
		if (chunk != null) {
			chunk.dirty = 0;
		}

		chunk = layers.get(0).chunkmap.get(chunkc);
		if (chunk != null) {
			chunk.dirty = 0;
		}

		//rand.setSeed(seed + Coord.get(xx, xx));

		//For each block in the current row
		for (int x = xx; x < xx + Chunk.size; x++) {
			rand.setSeed(seed + Coord.get(x, xx));

			//Init X variables
			//TODO better generation

			xHeight.put(x, xHeight.get(x, (int) (
					5f * MathUtils.sinDeg(x + rand.nextInt(24) - 12)
							- 4f * MathUtils.cosDeg(x * 0.25f + rand.nextInt(8) - 4)
							+ 2f * MathUtils.sinDeg(x * 2f + rand.nextInt(32) - 16)
							- 4f * MathUtils.cosDeg(MathUtils.sinDeg(x * 0.75f + rand.nextInt(48) - 28) * 90f)
			) + 7));

			xStone.put(x, xStone.get(x, rand.nextInt(5) + xHeight.get(x, 0)));

			rand.setSeed(seed + Coord.get(x, yy));

			//For each block in current chunk
			for (int y = yy; y < yy + Chunk.size; y++) {
				//Remove already existing blocks
				Array<Block> al = layers.get(1).get(Coord.get(x, y));
				if (al != null) {
					while (al.size > 0) {
						layers.get(1).remove(al.pop());
					}
				}
				al = layers.get(0).get(Coord.get(x, y));
				if (al != null) {
					while (al.size > 0) {
						layers.get(0).remove(al.pop());
					}
				}

				//Generate the tile at X, Y
				boolean cangen = ModManager.generateTile(GenLevel.this, xx, x, y, 1);
				if (cangen) {
					generateTile(xx, x, y, 1);
				}
				/*//Add / remove /* on beginning of line to disable / enable debugging.
				if (y == xHeight.get(x, 0)) {
					Block block = BlockType.getInstance("BlockDebug", x, y, layers.get(1));
					block.alpha = 0.25f;
					block.blending = true;
					layers.get(1).add(block);
				}
				/*
				*/
			}
		}

		dirtify = true;

		return true;
	}

	public void generateTile(int xx, int x, int y, int fg) {
		Layer lfg = layers.get(fg);
		Layer lbg = layers.get(fg-1);

		if (2 <= y && y < xHeight.get(x, 0)) {
			//Generate water.
			lfg.add(BlockType.getInstance("BlockWater", x, y, lfg));
			return;
		}

		if (y == xHeight.get(x, 0)) {
			//Generate surface (grass)
			lfg.add(BlockType.getInstance("BlockGrass", x, y, lfg));
			lbg.add(BlockType.getInstance("BlockGrass", x, y, lbg));
			return;
		}

		if (y > xHeight.get(x, 0)) {
			if (y < xStone.get(x, 0)) {
				//Generate surface (dirt)
				lfg.add(BlockType.getInstance("BlockDirt", x, y, lfg));
				lbg.add(BlockType.getInstance("BlockDirt", x, y, lbg));
			} else {
				//Generate everything underground.
				cavegen.generateFG(xx, x, y, fg);
				cavegen.generateBG(xx, x, y, fg);
				//lfg.add(BlockType.getInstance("BlockStone", x, y, lfg));
				//lbg.add(BlockType.getInstance("BlockStone", x, y, lbg));
			}
		}
	}

	public void loadChunk(long chunkc) {
		AsyncThread thread = map.asyncThread(false);
		if (thread != null && thread.left > 0) {
			return;
		}

		if (!stored.contains(chunkc)) {
			return;
		}

		DataChunk chunk = map.chunkmap.get(chunkc);
		if (chunk == null) {
			if (mapLoaded == null) {
				mapLoaded = map;
			} else if (mapLoaded.timestamp < map.timestamp) {
				mapLoaded = ShadowMap.loadFile(map.file);
			}
			chunk = mapLoaded.chunkmap.get(chunkc);
			if (chunk == null) {
				mapLoaded = ShadowMap.loadFile(map.file);
				chunk = mapLoaded.chunkmap.get(chunkc);
				if (chunk == null) {
					stored.removeValue(chunkc);
					return;
				}
			}
			map.chunkmap.put(chunkc, chunk);
		}
		ageChunks.put(chunkc, ageCurrent);

		for (int i = 0; i < chunk.objects.size; i++) {
			MapObject mo = chunk.objects.items[i];
			GameObject go = ShadowMap.convert(mo, this);
			if (!(go instanceof Player)) {
				go.layer.add(go);
			}
		}

		stored.removeValue(chunkc);
		generated.add(chunkc);
	}

	/**
	 * @return 0 when already stored; 1 when to clear; 3 when to save
	 */
	public int saveChunk(long chunkc) {
		if (stored.contains(chunkc)) {
			return 0;//00
		}

		DataChunk chunk = DataChunk.create(Coord.getX(chunkc), Coord.getY(chunkc), this, true);

		if (chunk == null) {
			generated.removeValue(chunkc);
			return 1;//01
		}

		map.chunkmap.put(chunkc, chunk);
		stored.add(chunkc);
		generated.removeValue(chunkc);

		return 3;//11
	}

	public void clearChunk(long chunkc) {
		Chunk chunk = mainLayer.chunkmap.get(chunkc);

		if (chunk == null) {
			return;
		}

		while (chunk.blocks.size > 0) {
			GameObject go = chunk.blocks.items[0];
			go.layer.remove(go);
		}

		//TODO remove entities in chunk, not added in chunk
		/*
		while (chunk.entities.size > 0) {
			chunk.layer.remove(chunk.entities.pop());
		}
		*/
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		Rectangle vp = Shadow.cam.camrec;
		ageCurrent++;
		Vector2 ppos = player.pos;
		generateChunks((int)(ppos.x - vp.width*2f), (int)(ppos.x + vp.width*2f),
				(int)(ppos.y - vp.height*2f), (int)(ppos.y + vp.height*2f));

		if ((storeAuto && ageCurrent%ageOffset == 0) || storeForce || storeAutoDelayed) {
			storeForce = false;
			AsyncThread thread = map.asyncThread();
			if (thread.left > 0) {
				//System.out.println("Left: "+thread.left);
				storeAutoDelayed = true;
			} else {
				storeAutoDelayed = false;
				thread.queue(new Runnable() {
					@Override
					public void run() {
						int chunks = 0;
						for (int i = 0; i < generated.size; i++) {
							long chunkc = generated.items[i];
							int age = ageChunks.get(chunkc, ageCurrent);
							if (age < ageCurrent - ageOffset) {
								int state = saveChunk(chunkc);
								if ((state & 1) == 1) {
									clearChunk(chunkc);
								}
								if ((state & 2) == 2) {
									chunks++;
								}
							}
						}
						if (chunks > 0) {
							Field[] fields = GenLevel.this.getClass().getFields();
							Field.setAccessible(fields, true);
							for (Field field : fields) {
								IsSaveable saveable = field.getAnnotation(IsSaveable.class);
								if (saveable != null) {
									try {
										map.params.put(field.getName(), field.get(GenLevel.this));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							System.out.println("Saving " + chunks + " chunks...");
							map.update(map.file, true);
						}
					}
				});
			}
		}
	}
	
}
