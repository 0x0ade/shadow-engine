package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.IntMap.Entry;

public abstract class LightEngine {
	
	public Level level;
	public static boolean inview = true;
	public static int espeed = 10;
	public static int bspeed = 5;
	public int etick = 0;
	public int btick = 0;
	
	protected static Rectangle objrec = new Rectangle();
	
	public LightEngine(Level level) {
		this.level = level;
	}
	
	public void tick() {
		if (Shadow.isAndroid) {
			return; //FIXME PERFORMANCE
		}
		boolean canEntity = etick > espeed;
		if (canEntity) {
			etick = 0;
		}
		boolean canBlock = btick > bspeed;
		if (canBlock) {
			btick = 0;
		}
		Layer ll = level.mainLayer;
		if (canBlock) {
			for (Block block : ll.blocks) {
				if (block == null) continue;
				objrec.set(block.pos.x-2f, block.pos.y-2f, block.rec.width+4f, block.rec.height+4f);
				if (inview && Shadow.cam.camrec.overlaps(objrec)) {
					setLight(block, level.mainLayer);
				}
			}
		}
		
		if (canEntity) {
			for (Entity entity : ll.entities) {
				if (entity == null) continue;
				objrec.set(entity.pos.x-4f, entity.pos.y-4f, entity.rec.width+8f, entity.rec.height+8f);
				if (inview && Shadow.cam.camrec.overlaps(objrec)) {
					setLight(entity, level.mainLayer);
				}
			}
		}
		etick++;
		btick++;
	}
	
	public abstract void setLight(Block b, Layer ll);
	public abstract void setLight(Entity e, Layer ll);
	
}
