package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap.Entry;

public class LightSystem {
	
	public Level level;
	public static boolean inview = true;
	public int speed = 10;
	public boolean canUpdate = false;
	public int tick = 0;
	
	protected static Rectangle viewport = new Rectangle();
	protected static Rectangle objrec = new Rectangle();
	
	public LightSystem(Level level) {
		this.level = level;
	}
	
	public void tick() {
		canUpdate = tick >= speed;
		
		if (canUpdate) {
			tick = 0;
		}
		
		tick++;
		
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
		if (!canUpdate) {
			return;
		}
		
		if (level.tickid < speed*2) {
			return;
		}
		
		if (inview) {
			objrec.set(go.pos.x, go.pos.y, go.rec.width, go.rec.height);
			if (!viewport.overlaps(objrec)) {
				return;
			}
		}
		
		boolean primaryLight = !(go instanceof Entity);
		boolean secondaryLight = go.light.a == 0 || go instanceof Entity;
		
		if (go.clearLight) {
			go.lightTint.set(ll.level.globalLight).mul(0.15f, 0.15f, 0.15f, 1f);
			//go.lightTint.set(0.1f, 0.1f, 0.1f, 1f);
			
			go.clearLight = false;
		}
		
		int cx = (int)go.pos.x;
		int cy = (int)go.pos.y;
		float r = 6.5f;
		float rsq = MathHelper.sq(r);
		float rp = 4f;
		float rpsq = MathHelper.sq(rp);
		
		float avgsun = (ll.level.globalLight.r + ll.level.globalLight.g + ll.level.globalLight.a) / 3f;
		
		for (float x = cx-r; x <= cx+r; x++) {
			for (float y = cy-r; y <= cy+r; y++) {
				float tmpradsq = MathHelper.distsq(cx, cy, x, y);
				if (tmpradsq <= rsq) {
					float tmprad = (float) Math.sqrt(tmpradsq);
					float f = 1f/rsq;
					Array<Block> al = ll.get(Coord.get(x, y));
					if (secondaryLight && tmpradsq <= rpsq) {
						float fp = 1f/rpsq;
						//Passive lighting - X checks for light source and adapts to it.
						//If go is entity it uses it to adapt to light as active lighting can't light entities.
						//If go is block AND go.color.a is 0 this is used to check for sun.
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
						if (es != 0 && !primaryLight) {
							go.lightTint.add(emit.mul(1f/es).mul(fp));
						}
						if (bs == 0) {
							go.lightTint.add(sun.mul(1f/ps).mul((fp*avgsun)*0.6275f));
						} else {
							go.lightTint.add(dark.mul(1f/bs).mul(fp));
						}
					}
					//Sidenote: No, I didn't forget the if-check. Entities cast light but don't get lighted by blocks via primaryLight.
					//Active lighting - Light source casts light to blocks around itself, strength of light being dependent of the radius.
					//If go is block it uses it as primary lighting, casting it's light to other blocks.
					//If go is entity it uses it to cast it's light to blocks. 
					//Unfournately it does NOT cast light to entities so entities use the secondary lighting method.
					if (go.light.a > 0f && al != null && al.size != 0) {
						go.lightTint.set(go.light);
						for (Block bb : al) {
							if (bb.light.a > 0f) {
								continue;
							}
							emit.set(go.light);
							bb.lightTint.add(emit.mul((1f-tmpradsq/rsq)*go.light.a));
						}
					}
				}
			}
		}
		
		go.lightTint.a = 1f;
		go.cantint = true;
		go.clearLight = true;
	}
	
}
