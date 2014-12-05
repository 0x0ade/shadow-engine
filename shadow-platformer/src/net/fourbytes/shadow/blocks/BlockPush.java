package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.network.ClientLevel;
import net.fourbytes.shadow.network.DataMapUpdate;
import net.fourbytes.shadow.network.ServerLevel;

import java.util.Random;

public class BlockPush extends BlockType {

	public TextureRegion tex;

    @IsSaveable
	public Vector2 lastpos;
    @IsSaveable
	public float gframe = 0;
    @IsSaveable
	public float oframe = 0;
    @IsSaveable
	public float pframe = 0;
	
	public BlockPush() {
	}
	
	public static Random rand = new Random();

	@Override
	public void init() {
		blending = false;
		tickAlways = true;
		light.set(0.25f, 0.5f, 0.75f, 1f);
	}

	@Override 
	public void tick(float delta) {
		gframe -= delta * 60f;
		if (gframe <= 0) {
			boolean free = true;
			
			/*for (Block b : block.layer.blocks) {
				if (!b.solid) continue;
				float rad = 0.1f;
				Rectangle er = new Rectangle(block.pos.x+rad, block.pos.y+1+rad, block.rec.width-rad*2, block.rec.height-rad*2);
				Rectangle or = new Rectangle(b.pos.x, b.pos.y, b.rec.width, b.rec.height);
				if (or.overlaps(er)) {
					free = false;
				}
				if (!free) break;
			}*/
			
			Array<Block> al = layer.get(Coord.get(pos.x, pos.y+1));
			if (al != null) {
				for (int i = 0; i < al.size; i++) {
					Block b = al.items[i];
					if (b instanceof BlockFluid) {
						if (((BlockFluid)b).height > 12) {
							free = false;
							break;
						} else {
							continue;
						}
					}
					if (!b.solid) continue;
					free = false;
					break;
				}
			}
			
			if (free) {
				if (lastpos == null) {
					lastpos = new Vector2();
				}
				lastpos.set(pos);
				int lastx = (int)pos.x;
				int lasty = (int)pos.y;
                pos.y += 1f;
				int newx = (int)pos.x;
				int newy = (int)pos.y;
				layer.move(this, Coord.get(lastx, lasty), Coord.get(newx, newy));
				gframe = 18f;
				oframe = 15f;
			}
		}
		
		if (oframe > 0) {
			oframe -= delta * 60f;
		}
		
		if (lastpos != null) {
			if (oframe > 0) {
				renderoffs.set(lastpos.x-pos.x, lastpos.y-pos.y, 0f, 0f);
				float fac = 383f/512f;
				for (int i = 0; i < 15-oframe; i++) {
					renderoffs.x *= fac;
					renderoffs.y *= fac;
				}
			} else {
				renderoffs.set(0f, 0f, 0f, 0f);
			}
		}
		
		if (pframe > 0) {
			pframe -= delta * 60f;
		}
		
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.getTextureRegion("block_push") : tex;
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			if (Coord.get1337((int)(p.pos.y)) == (int)(pos.y+p.rec.height) && pframe <= 0) {
			//if (pframe <= 0) {
				int dir = p.facingLeft?-1:1;
				push(dir, 0);
			}
		}
	}
	
	public boolean push(int dir, int count) {
		boolean free = true;
		
		/*for (Block b : block.layer.blocks) {
			if (!b.solid) continue;
			float rad = 0.1f;
			Rectangle er = new Rectangle(block.pos.x+dir+rad, block.pos.y+rad, block.rec.width-rad*2, block.rec.height-rad*2);
			Rectangle or = new Rectangle(b.pos.x, b.pos.y, b.rec.width, b.rec.height);
			if (or.overlaps(er)) {
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockPush && count < 6) {
					((BlockPush)((TypeBlock)b).type).push(dir, count+1);
					free = false;
					pframe = 4;
				} else {
					free = false;
				}
			}
			if (!free) break;
		}*/

		Array<Block> al = layer.get(Coord.get(pos.x+dir, pos.y));
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b instanceof BlockFluid) {
					free = false;
					break;
				}
				if (!b.solid) continue;
				if (b instanceof BlockPush && count < 6) {
					((BlockPush)b).push(dir, count+1);
					free = false;
					pframe = 4f;
					break;
				}
				free = false;
				break;
				//if (!free) break;
			}
		}
		
		if (free) {
			if (lastpos == null) {
				lastpos = new Vector2();
			}
			lastpos.set(pos);
			int lastx = (int)pos.x;
			int lasty = (int)pos.y;
            pos.x += dir;
			int newx = (int)pos.x;
			int newy = (int)pos.y;
			layer.move(this, Coord.get(lastx, lasty), Coord.get(newx, newy));
			renderoffs.set(lastpos.x-pos.x, lastpos.y-pos.y, 0f, 0f);
			oframe = 15f;
			pframe = 2f;

            if (Shadow.level instanceof ClientLevel || Shadow.level instanceof ServerLevel) {
                DataMapUpdate dmu = new DataMapUpdate();
                dmu.mode = DataMapUpdate.MapUpdateModes.UPDATE;
                dmu.object = ShadowMap.convert(this);
                if (Shadow.level instanceof ClientLevel) {
                    Shadow.client.send(dmu);
                } else {
                    Shadow.server.send(dmu);
                }
            }
		}
		
		return free;
	}
	
}
