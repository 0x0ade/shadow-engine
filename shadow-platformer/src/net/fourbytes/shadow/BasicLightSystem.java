package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class BasicLightSystem extends LightSystem {
	
	public BasicLightSystem(Level level) {
		super(level);
	}

	@Override
	public void setLight(Block b, Layer ll) {
		setLight((GameObject) b, ll);
	}

	@Override
	public void setLight(Entity e, Layer ll) {
		setLight((GameObject) e, ll);
	}
	
	protected final static Color sun = new Color(1f, 1f, 1f, 1f);
	protected final static Color dark = new Color(1f, 1f, 1f, 1f);
	protected final static Color emit = new Color(1f, 1f, 1f, 1f);
	protected final static Color tmpc = new Color(1f, 1f, 1f, 1f);
	
	
	public void setLight(GameObject go, Layer ll) {
		//go.lightTint.set(0f, 0f, 0f, 1f);
		go.lightTint.set(ll.level.globalLight).mul(0.2f, 0.2f, 0.2f, 1f);
		//Coord c = Garbage.coord;
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
					//Coord cc = Garbage.coorda;
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
