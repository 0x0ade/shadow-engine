package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap.Entry;

public class LightSystem {
	
	public Level level;
	public static boolean inview = true;
	public int espeed = 10;
	public int bspeed = 5;
	public boolean canEntity = false;
	public boolean canBlock = false;
	public int etick = 0;
	public int btick = 0;
	
	protected static Rectangle viewport = new Rectangle();
	protected static Rectangle objrec = new Rectangle();
	
	public LightSystem(Level level) {
		this.level = level;
	}
	
	public void tick() {
		canEntity = etick >= espeed;
		canBlock = btick >= bspeed;
		
		if (canEntity) {
			etick = 0;
		}
		if (canBlock) {
			btick = 0;
		}
		
		etick++;
		btick++;
		
		viewport.set(Shadow.cam.camrec);
		float f = 5f;
		viewport.x -= f;
		viewport.y -= f;
		viewport.width += f*2;
		viewport.height += f*2;
	}
	
	protected final static Color sun = new Color(1f, 1f, 1f, 1f);
	protected final static Color dark = new Color(1f, 1f, 1f, 1f);
	protected final static Color emit = new Color(1f, 1f, 1f, 1f);
	protected final static Color tmpc = new Color(1f, 1f, 1f, 1f);
	
	public void setLight(GameObject go, Layer ll) {
		if ((go instanceof Block && !canBlock) || (go instanceof Entity && !canEntity)) {
			return;
		}
		
		if (inview) {
			objrec.set(go.pos.x, go.pos.y, go.rec.width, go.rec.height);
			if (!viewport.overlaps(objrec)) {
				return;
			}
		}
		
		//go.lightTint.set(0f, 0f, 0f, 1f);
		go.lightTint.set(ll.level.globalLight).mul(0.2f, 0.2f, 0.2f, 1f);
		int cx = (int)go.pos.x;
		int cy = (int)go.pos.y;
		float r = 4;
		float rsq = MathHelper.sq(r);
		for (float x = cx-r; x <= cx+r; x++) {
			for (float y = cy-r; y <= cy+r; y++) {
				float tmpradsq = MathHelper.distsq(cx, cy, x, y);
				if (tmpradsq<=rsq) {
					float tmprad = (float) Math.sqrt(tmpradsq);
					float f = 1f/rsq;
					Array<Block> al = ll.get(Coord.get(x, y));
					int bs = 0;
					int es = 0;
					int ps = 0;
					//sun.set(1f, 1f, 1f, 1f);
					sun.set(ll.level.globalLight);
					dark.set(0f, 0f, 0f, 1f);
					emit.set(1f, 1f, 1f, 1f);
					if (al != null && al.size != 0) {
						for (Block bb : al) {
							if (bb.light.a > 0f) {
								es++;
								if (es == 1) {
									emit.set(bb.light);
								} else {
									emit.add(bb.light);
								}
							}
							if (bb.passSunlight) {
								ps++;
								if (ps == 1) {
									sun.set(bb.tintSunlight);
								} else {
									sun.add(bb.tintSunlight);
								}
							} else {
								bs++;
								if (bs == 1) {
									dark.set(bb.tintDarklight);
								} else {
									dark.add(bb.tintDarklight);
								}
							}
						}
					} else {
						ps = 0;
					}
					if (es != 0) {
						go.lightTint.add(emit.mul(1f/es).mul(f));
					} else if (bs == 0) {
						go.lightTint.add(sun.mul(1f/ps).mul(f));
					} else {
						go.lightTint.add(dark.mul(1f/bs).mul(f));
					}
				}
			}
		}
		go.lightTint.a = 1f;
		go.cantint = true;
	}
	
}
