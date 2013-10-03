package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class LightSystem {
	
    public Level level;
  	public int speed = 10;
	public boolean canUpdate = false;
	public int tick = 0;

	public LightSystem(Level level) {
		this.level = level;
	}
	
	public void tick() {
		if (Shadow.isAndroid) {
			//return;//TODO Fix performance - duh
		}
		
		canUpdate = tick >= speed;
		
		if (canUpdate && level.tickid >= speed*2) {
			for (GameObject go : level.mainLayer.inView) {
				setLight(go, level.mainLayer, true);
			}
			
			for (GameObject go : level.mainLayer.inView) {
				setLight(go, level.mainLayer, false);
			}
			
			tick = 0;
		}
		
		tick++;
	}
	
	protected final static Color sun = new Color(1f, 1f, 1f, 1f);
	protected final static Color dark = new Color(1f, 1f, 1f, 1f);
	protected final static Color emit = new Color(1f, 1f, 1f, 1f);
	protected final static Color tmpc = new Color(1f, 1f, 1f, 1f);

	public void setLight(GameObject go, Layer ll, boolean clearLight) {
		if (clearLight) {
			go.lightTint.set(ll.level.globalLight).mul(0.15f, 0.15f, 0.15f, 1f);
			return;
		}
		
		int cx = (int)go.pos.x;
		int cy = (int)go.pos.y;
		float r = 6.5f;
		float rsq = MathHelper.sq(r);
		
		float avgsun = (ll.level.globalLight.r + ll.level.globalLight.g + ll.level.globalLight.a) / 3f;
		
		for (float x = cx-r; x <= cx+r; x++) {
			for (float y = cy-r; y <= cy+r; y++) {
				float tmpradsq = MathHelper.distsq(cx, cy, x, y);
				if (tmpradsq <= rsq) {
					//float tmprad = (float) Math.sqrt(tmpradsq);
					Array<Block> al = ll.get(Coord.get(x, y));
					float fsun = (1f/rsq)*avgsun*0.6275f;
					float fdark = 1f/rsq;
					float femit= 1f-tmpradsq/rsq;
					//Passive lighting - X checks for light source and adapts to it.
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
									sun.set(tmpc.set(ll.level.globalLight).mul(bb.tintSunlight));
								} else {
									sun.add(tmpc.set(ll.level.globalLight).mul(bb.tintSunlight));
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
						go.lightTint.add(emit.mul(1f).mul(femit));
					}
					if (bs == 0) {
						go.lightTint.add(sun.mul(1f/ps).mul(fsun));
					} else {
						go.lightTint.add(dark.mul(1f/bs).mul(fdark));
					}
				}
			}
		}
		
		go.lightTint.a = 1f;
		go.cantint = true;
	}
	
}
