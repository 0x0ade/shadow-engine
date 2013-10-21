package net.fourbytes.shadow.genlevel;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.utils.MathHelper;

public class DefaultCaveGen extends CaveGen {

	public Array<CaveObject> fgobjs = new Array<CaveObject>();
	public Array<CaveObject> bgobjs = new Array<CaveObject>();
	public LongMap<String> replaced = new LongMap<String>();

	public DefaultCaveGen(GenLevel level) {
		super(level);

		fgobjs.add(new CaveObject("BlockStone", -1f, 0));
		fgobjs.add(new CaveObject("BlockDirt", 25000000f, 0));

		fgobjs.add(new CaveObject("Bomb::6", 3000000f, 3));//Too low minimum depth causes holes in surface
		fgobjs.add(new CaveObject("Bomb:BlockWater:2", 150000f, 50));
		fgobjs.add(new CaveObject("Bomb:BlockLava:3", 100000f, 100));

		//--------------------------------

		bgobjs.add(new CaveObject("BlockStone", -1f, 0));
		bgobjs.add(new CaveObject("BlockDirt", 25000000f, 0));
	}

	@Override
	public String getBlock(int xx, int x, int y, boolean isFG) {
		if (isFG && replaced.containsKey(Coord.get(x, y))) {
			return replaced.get(Coord.get(x, y));
		}

		int yo = level.xStone.get(x, 0);

		String got = null;
		float altprob = 0f;
		String alt = null;
		float random = level.rand.nextFloat()*100000000f;

		for (CaveObject obj : isFG?fgobjs:bgobjs) {
			if (obj.probability < altprob) {
				alt = obj.type;
				altprob = obj.probability;
				continue;
			}

			if (random < obj.probability && y+yo >= obj.depth) {
				got = obj.type;
			}
		}

		return got==null?alt:got;
	}

	@Override
	public void handle(int xx, int x, int y, int ln, String got) {
		if (got.startsWith("Block")) {
			level.layers.get(ln).add(BlockType.getInstance(got, x, y, level.layers.get(ln)));
		} else {
			//TODO Handle non-blocks
			if (got.startsWith("Bomb:")) {
				String[] split = got.split(":");
				bomb(x, y, ln, level.rand.nextInt(Integer.parseInt(split[2]))+1, split[1]);
			}
		}
	}

	public void bomb(int x, int y, int ln, int rad, String type) {
		Layer layer = level.layers.get(ln);
		for (float xx = x - rad; xx <= x + rad; xx++) {
			int yo = level.xStone.get((int)xx, 0);//YO is just a funny thing to play with...
			for (float yy = y + yo - rad; yy <= y + yo + rad; yy++) {
				if (MathHelper.distsq(x, y, xx, yy) <= MathHelper.sq(rad)) {
					replaced.put(Coord.get(xx, yy), type);

					Array<Block> blocks = layer.get(Coord.get(xx, yy));
					if (blocks != null) {
						for (Block b : blocks) {
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
