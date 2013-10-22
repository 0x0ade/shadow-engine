package net.fourbytes.shadow.genlevel;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.LongArray;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;
import net.fourbytes.shadow.mod.ModLoader;
import net.fourbytes.shadow.utils.MathHelper;

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
	public IntIntMap segHeight = new IntIntMap();
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

		segHeight.put(0, 0);

		generateChunks(-25, 25, -25, 25);
		
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
		boolean generated = false;
		for (int xx = fromx; xx < tox; xx+=segSize) {
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

		//Init segment variables
		//Segment height
		int h;
		if (segHeight.containsKey(xx/segSize)) {
			//Get segment height
			h = segHeight.get(xx/segSize, 0);
		} else {
			//Set new segment height
			if (segHeight.containsKey(xx/segSize - 1) && segHeight.containsKey(xx/segSize + 1)) {
				//Set height relative to segments left and right to current one
				int lefth = segHeight.get(xx/segSize - 1, 0);
				int righth = segHeight.get(xx/segSize + 1, 0);
				h = (lefth+righth)/2;
			} else if (segHeight.containsKey(xx/segSize - 1) || segHeight.containsKey(xx/segSize + 1)) {
				//Set height relative to segment left or right to current one
				int baseh = segHeight.get(xx/segSize - 1, segHeight.get(xx/segSize + 1, 0));
				int offs = rand.nextInt(5)-2;
				h = baseh + offs;
				if (h < -15 || h > 15) {
					h = baseh - offs;
				}
			} else {
				//Set random height
				h = rand.nextInt(5)-2;
				h = rand.nextInt(5)-2+h;
			}
			segHeight.put(xx/segSize, h);
		}

		//For each block in the current row
		for (int x = xx; x < xx+segSize; x++) {
			rand.setSeed(seed+Coord.get(x, xx));

			//Init X variables
			//X height
			int xh = xHeight.get(x, rand.nextInt(2) + h);
			xHeight.put(x, xh);

			//X stone height
			int xsh = xStone.get(x, rand.nextInt(3) + 5 + xh);
			xStone.put(x, xsh);

			rand.setSeed(seed+Coord.get(x, yy));

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
			}
		}
		return true;
	}
	
	public void generateTile(int xx, int x, int y, int fg) {
		if (y >= 2 && y < xHeight.get(x, 0)+1 && 0 < segHeight.get(xx/segSize, 0)) {
			//Generate water.
			layers.get(fg).add(BlockType.getInstance("BlockWater", x, y, layers.get(fg)));
			return;
		}

		if (y == xHeight.get(x, 0) || (y > 2 && y == xHeight.get(x, 0)+1)) {
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
				//TODO Write some simple cave generator
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
		boolean generated = generateChunks((int)(player.pos.x - vp.width*1.5f), (int)(player.pos.x + vp.width*1.5f),
				(int)(player.pos.y - vp.height*1.5f), (int)(player.pos.y + vp.height*1.5f));
	}
	
	public void bomb(Layer layer, float x, float y, float radius, LongArray xd, String type) {
		for(float xx=x-radius;xx<=x+radius;xx+=1) {
			for(float yy=y-radius;yy<=y+radius;yy+=1) {
				if (MathHelper.distsq(x, y, xx, yy)<=MathHelper.sq(radius)) {
					Array<Block> al = layer.get(Coord.get(xx, yy));
					xd.add(Coord.get(xx, yy)); //adding c would cause reference problems
					if (al != null) {
						//al = (Vector<Block>) al.clone();
						for (Block b : al) {
							layer.remove(b);
						}
					}
					if (!type.isEmpty()) {
						layer.add(BlockType.getInstance(type, xx, yy, layer));
					}
				}
			}
		}
	}
	
}
