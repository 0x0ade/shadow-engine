package net.fourbytes.shadow.genlevel;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.LongArray;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;
import net.fourbytes.shadow.mod.ModLoader;

import java.util.Random;

public class GenLevel extends Level {

	public static int segSize = 8;

	@Saveable
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

	@Saveable
	public LongArray generated = new LongArray();
	@Saveable
	public IntIntMap xHeight = new IntIntMap();
	@Saveable
	public IntIntMap xStone = new IntIntMap();

	public CaveGen cavegen = new DefaultCaveGen(this);

	public GenLevel() {
		tiledh = 0;
		hasvoid = false;

		fillLayer(0);
		fillLayer(1);

		float d = 0.5f;
		layers.get(0).tint.set(d, d, d, 1f);

		generateChunks(-3*segSize, 3*segSize, -3*segSize, 3*segSize);
		
		Player p = new Player(new Vector2(0f, -5f), layers.get(1));
		layers.get(1).add(p);
		player = p;
		
		c = new Cursor(new Vector2(0f, 0f), layers.get(1));

		ready = true;
	}
	
	public boolean generateChunks(int fromx, int tox, int fromy, int toy) {
		fromx = (fromx/segSize)*segSize;
		tox = (tox/segSize)*segSize;
		fromy = (fromy/segSize)*segSize;
		toy = (toy/segSize)*segSize;
		int midx = fromx + (tox - fromx) / 2;
		midx = (midx/segSize)*segSize;

		boolean generated = false;

		for (int xx = midx; xx < tox; xx+=segSize) {
			for (int yy = fromy; yy < toy; yy+=segSize) {
				boolean cgenerated = generateChunk(xx, yy);
				if (cgenerated) {
					generated = true;
				}
			}
		}

		for (int xx = midx; xx >= fromx; xx-=segSize) {
			for (int yy = fromy; yy < toy; yy+=segSize) {
				boolean cgenerated = generateChunk(xx, yy);
				if (cgenerated) {
					generated = true;
				}
			}
		}

		return generated;
	}
	
	public boolean generateChunk(int xx, int yy) {
		//Check whether current chunk already generated
		long chunkc = Coord.get(xx / segSize, yy / segSize);
		if (generated.contains(chunkc)) {
			return false;
		} else {
			generated.add(chunkc);
		}

		rand.setSeed(seed+Coord.get(xx, xx));

		//For each block in the current row
		for (int x = xx; x < xx+segSize; x++) {
			rand.setSeed(seed+Coord.get(x, xx));

			//Init X variables
			//TODO better generation

			xHeight.put(x, xHeight.get(x, (int)(
					5f*MathUtils.sinDeg(x+rand.nextInt(24)-12)
							- 4f*MathUtils.cosDeg(x*0.25f+rand.nextInt(8)-4)
							+ 2f*MathUtils.sinDeg(x*2f+rand.nextInt(32)-16)
							- 4f*MathUtils.cosDeg(MathUtils.sinDeg(x*0.75f+rand.nextInt(48)-28)*90f)
			) + 7));

			xStone.put(x, xStone.get(x, rand.nextInt(5)+xHeight.get(x, 0)));

			rand.setSeed(seed + Coord.get(x, yy));

			//For each block in current chunk
			for (int y = yy; y < yy+segSize; y++) {
				//Remove already existing blocks
				Array<Block> al = layers.get(1).get(Coord.get(x, y));
				if (al != null) {
					for (Block b : al) {
						layers.get(1).remove(b);
					}
				}
				al = layers.get(0).get(Coord.get(x, y));
				if (al != null) {
					for (Block b : al) {
						layers.get(0).remove(b);
					}
				}

				//Generate the tile at X, Y
				boolean cangen = ModLoader.generateTile(this, xx, x, y, 1);
				if (cangen) {
					generateTile(xx, x, y, 1);
				}
				//Add / remove /* on beginning of line to disable / enable debugging.
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
		return true;
	}
	
	public void generateTile(int xx, int x, int y, int fg) {
		if (2 <= y && y < xHeight.get(x, 0)) {
			//Generate water.
			layers.get(fg).add(BlockType.getInstance("BlockWater", x, y, layers.get(fg)));
			return;
		}

		if (y == xHeight.get(x, 0)) {
			//Generate surface (grass)
			layers.get(fg).add(BlockType.getInstance("BlockGrass", x, y, layers.get(fg)));
			layers.get(fg-1).add(BlockType.getInstance("BlockGrass", x, y, layers.get(fg-1)));
			return;
		}

		if (y > xHeight.get(x, 0)) {
			if (y < xStone.get(x, 0)) {
				//Generate surface (dirt)
				layers.get(fg).add(BlockType.getInstance("BlockDirt", x, y, layers.get(fg)));
				layers.get(fg-1).add(BlockType.getInstance("BlockDirt", x, y, layers.get(fg-1)));
			} else {
				//Generate everything underground.
				cavegen.generateFG(xx, x, y, fg);
				cavegen.generateBG(xx, x, y, fg);
				//layers.get(fg).add(BlockType.getInstance("BlockStone", x, y, layers.get(fg)));
				//layers.get(fg-1).add(BlockType.getInstance("BlockStone", x, y, layers.get(fg-1)));
			}
			return;
		}
	}

	@Override
	public void tick() {
		super.tick();
		Rectangle vp = Shadow.cam.camrec;
		generateChunks((int)(player.pos.x - vp.width*1.5f), (int)(player.pos.x + vp.width*1.5f),
				(int)(player.pos.y - vp.height*1.5f), (int)(player.pos.y + vp.height*1.5f));
	}
	
}
