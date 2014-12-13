package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.Input.TouchPoint;
import net.fourbytes.shadow.Input.TouchPoint.TouchMode;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.map.MapObject;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.network.ClientLevel;
import net.fourbytes.shadow.network.DataMapUpdate;
import net.fourbytes.shadow.network.ServerLevel;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.systems.ITimeDaySystem;
import net.fourbytes.shadow.utils.Garbage;

public class Cursor extends Entity {
	
	public int id = -1;
	public boolean render = false;
	public Color color;

	public int layerid = 0;
	public int playerln = Byte.MIN_VALUE;

	//public static Array<Particle> pps = new Array<Particle>(Particle.class);
	
	public Cursor(Vector2 position, Layer layer) {
		this(position, layer, -1);
	}
	
	public Cursor(Vector2 position, Layer layer, int id) {
		super(position, layer);
		this.id = id;
		solid = false;
		color = new Color(1f, 1f, 1f, 0.5f);
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("white");
	}
	
	public int button;

    @Override
	public void tick(float delta) {
		if (playerln == Byte.MIN_VALUE) {
			for (IntMap.Entry<Layer> entry : layer.level.layers.entries()) {
				int ln = entry.key;
				Layer ll = entry.value;

				if (ll.entities.contains(layer.level.player, true)) {
					playerln = ln;
					break;
				}
			}
		}

		layer.level.fillLayer(playerln + layerid);
		layer = layer.level.layers.get(playerln + layerid);

		TouchPoint tp = Input.touches.get(id);
		if (tp != null && tp.touchmode == TouchMode.Cursor) {
			pos.set(calcPos(tp.pos));
			oldpos = tp.pos;
			
			button = tp.button;
			
			switch (tp.button) {
			case -1: amb(tp, true); break;
			case 0: lmb(tp, true); break;
			case 1: rmb(tp, true); break;
			case 2: mmb(tp, true); break;
			}
			
			render = true;
		} else {
			downtick = 0;
			switch (button) {
			case -1: amb(tp, false); break;
			case 0: lmb(tp, false); break;
			case 1: rmb(tp, false); break;
			case 2: mmb(tp, false); break;
			}
		}
		if (tp == null && id != -1) {
			layer.level.cursors.removeValue(this, true);
		}
		if (oldpos != null && id == -1) {
			pos.set(calcPos(oldpos));
		}
		scroll(0);
	}
	
	int downtick = 0;
	boolean amb = false;
	int amode = 0;
	boolean lmb = false;
	boolean rmb = false;
	boolean mmb = false;
	
	public void amb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && !amb) {
			amode = 0;
		}
        if (amode == 0) {
            Array<Block> blocks = layer.get(Coord.get(pos.x, pos.y));
            if (blocks != null && blocks.size > 0) {
                amode = 2;
            } else {
                amode = 1;
            }
        }
		if (isDown && amb) {
			switch (amode) {
			case 0: break;
			case 1: lmb(point, true); break;
			case 2: rmb(point, true); break;
            default: break;
			}
		}
		amb = isDown;
	}
	
	public void lmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!lmb || downtick > 20)) {
			Block b = BlockType.getInstance("BlockPush", pos.x, pos.y, layer);
			b.layer.add(b);

            if (layer.level instanceof ClientLevel || layer.level instanceof ServerLevel) {
                DataMapUpdate dmu = new DataMapUpdate();
                dmu.object = ShadowMap.convert(b);
                dmu.mode = DataMapUpdate.MapUpdateModes.ADD;
                if (layer.level instanceof ClientLevel) {
                    Shadow.client.send(dmu);
                } else {
                    Shadow.server.send(dmu);
                }
            }

			/*for (int i = 0; i < pps.size; i++) {
				Particle pp = pps.items[i];
				pp.layer.remove(pp);
			}
			pps.clear();*/
		}
		lmb = isDown;
	}
	
	public void rmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!rmb || downtick > 20)) {
			Array<Block> blocks = layer.get(Coord.get(pos.x, pos.y));
			if (blocks != null) {
				for (Block b : blocks) {
                    b.pixelify();
                    b.layer.remove(b);
				}

                if (layer.level instanceof ClientLevel || layer.level instanceof ServerLevel) {
                    DataMapUpdate dmu = new DataMapUpdate();
                    dmu.object = new MapObject();
                    dmu.object.x = pos.x;
                    dmu.object.y = pos.y;
                    dmu.object.layer = layer.level.layers.findKey(layer, true, Integer.MAX_VALUE);
                    dmu.mode = DataMapUpdate.MapUpdateModes.REMOVE;
                    if (layer.level instanceof ClientLevel) {
                        Shadow.client.send(dmu);
                    } else {
                        Shadow.server.send(dmu);
                    }
                }
			}
			//Entity e = new MobTest(new Vector2(pos), layer);
			//e.layer.add(e);

            /*
			Vector2 pos = Garbage.vec2s.getNext();
			pos.set(point.pos);
			float cx = Shadow.cam.camrec.x;
			float cy = Shadow.cam.camrec.y;
			float mx = (pos.x * (Shadow.vieww/Shadow.dispw)) * Shadow.cam.cam.zoom;
			float my = (pos.y * (Shadow.viewh/Shadow.disph)) * Shadow.cam.cam.zoom;
			float tx = mx + cx;
			float ty = my + cy;
			pos.set(tx, ty);

			for (int i = 0; i < pps.size; i++) {
				Particle pp = pps.items[i];
				float f = pp.pos.dst(pos) / 2f;
				if (f >= 1f) {
					f = 1f;
				}
				float m = f;
				f *= 0.1f;
				m *= 0.5f;
				if (m < 0.15f) {
					m = 0.15f;
				}
				pp.movement.x += (pos.x - pp.pos.x) * f;
				pp.movement.y += (pos.y - pp.pos.y) * f;
				if (pp.movement.x > m) {
					pp.movement.x = m;
				}
				if (pp.movement.y > m) {
					pp.movement.y = m;
				}
				if (pp.movement.x < -m) {
					pp.movement.x = -m;
				}
				if (pp.movement.y < -m) {
					pp.movement.y = -m;
				}
			}
			*/
		}
		rmb = isDown;
	}

	public void mmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!mmb || downtick > 20)) {
			Vector2 ppos = Garbage.vec2s.getNext();
			Color color = Garbage.colors.getNext();
			color.set(0.75f, 0.5f, 0.25f, 1f);
			for (float dx = 0f; dx <= 1f; dx += MathUtils.random(0.3f, 0.7f)) {
				for (float dy = 0f; dy <= 1f; dy += MathUtils.random(0.3f, 0.7f)) {
					ppos.set(pos).add(dx, dy);
					color.mul(1f + MathUtils.random(-0.2f, 0.2f));
					color.a = 1f - MathUtils.random(0.15f);

					Particle pp = layer.level.systems.get(IParticleManager.class).create("PixelParticle", ppos, layer.level.player.layer, color, 0.0775f, 0);
					//Particle pp = layer.level.particles.create("PixelParticle", ppos, layer.level.player.layer, color, 0.0775f, -1);
					//Particle pp = layer.level.particles.create("RainParticle", ppos, layer.level.player.layer, null, 0.0775f, 0);
					//pp.objgravity = 0f;
					//pp.slowdown = 0f;
					//pp.pos.add(rec.width / 2f, rec.height / 2f);
					pp.light.set(pp.baseColors[pp.imgIDs[0]]).a = 0.1f;
					//pp.solid = true;
					pp.layer.add(pp);
					//pps.add(pp);

                    if (layer.level instanceof ClientLevel || layer.level instanceof ServerLevel) {
                        DataMapUpdate dmu = new DataMapUpdate();
                        dmu.object = ShadowMap.convert(pp);
                        dmu.mode = DataMapUpdate.MapUpdateModes.ADD;
                        if (layer.level instanceof ClientLevel) {
                            Shadow.client.send(dmu);
                        } else {
                            Shadow.server.send(dmu);
                        }
                    }
				}
			}
		}
		mmb = isDown;
	}
	
	Vector2 oldpos = new Vector2();
	final static Vector2 ppos = new Vector2();
	
	public Vector2 calcPos(Vector2 apos) {
		oldpos.set(apos);
		Vector2 pos = ppos;
		pos.set(apos);
		float g = 1f;
		float cx = Shadow.cam.camrec.x;
		float cy = Shadow.cam.camrec.y;
		float mx = (pos.x * (Shadow.vieww/Shadow.dispw)) * Shadow.cam.cam.zoom;
		float my = (pos.y * (Shadow.viewh/Shadow.disph)) * Shadow.cam.cam.zoom;
		float tx = mx + cx;
		float ty = my + cy;
		float otx = tx;
		float oty = ty;
		tx = (int) (tx / g);
		ty = (int) (ty / g);
		tx *= g;
		ty *= g;
		if (otx < 0) {
			tx -= g;
		}
		if (oty < 0) {
			ty -= g;
		}
		pos.set(tx, ty);
		return pos;
	}
	
	@Override
	public void preRender() {
		super.preRender();
		if (!render) {
			images[0].setColor(1f, 1f, 1f, 0f);
			alpha = 0f;
		} else {
			images[0].setColor(color);
			alpha = 1f;
		}
	}
	
	@Override
	public void render() {
		if (render) {
			super.render();
		}
	}

	public void scroll(int amount) {
        ITimeDaySystem time = layer.level.systems.get(ITimeDaySystem.class);
        if (time != null) {
            time.setTime(time.getTime() + amount);
        }
	}
	
}
