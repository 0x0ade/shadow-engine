package net.fourbytes.shadow.blocks;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Garbage;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Mob;
import net.fourbytes.shadow.Player;
import net.fourbytes.shadow.TypeBlock;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BlockPush extends BlockType {
	
	Vector2 lastpos;
	int gframe = 0;
	int oframe = 0;
	int pframe = 0;
	
	public BlockPush() {
	}
	
	public static Random rand = new Random();
	
	@Override 
	public void tick() {
		block.interactive = true;
		
		gframe--;
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
			
			Array<Block> al = block.layer.get(Coord.get(block.pos.x, block.pos.y+1));
			if (al != null) {
				for (Block b : al) {
					if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
						if (((BlockFluid)((TypeBlock)b).type).height > 12) {
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
				lastpos.set(block.pos);
				int lastx = (int)block.pos.x;
				int lasty = (int)block.pos.y;
				block.pos.add(0, 1);
				int newx = (int)block.pos.x;
				int newy = (int)block.pos.y;
				block.layer.move(block, Coord.get(lastx, lasty), Coord.get(newx, newy));
				gframe = 18;
				oframe = 15;
			}
		}
		
		if (oframe > 0) {
			oframe--;
		}
		
		if (lastpos != null) {
			if (oframe > 0) {
				block.renderoffs.set(lastpos.x-block.pos.x, lastpos.y-block.pos.y, 0f, 0f);
				float fac = 383f/512f;
				for (int i = 0; i < 15-oframe; i++) {
					block.renderoffs.x *= fac;
					block.renderoffs.y *= fac;
				}
			} else {
				block.renderoffs.set(0f, 0f, 0f, 0f);
			}
		}
		
		if (pframe > 0) {
			pframe--;
		}
		
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("block_push");
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			if (Coord.get1337((int)(p.pos.y)) == (int)(block.pos.y+p.rec.height) && pframe <= 0) {
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
		
		Array<Block> al = block.layer.get(Coord.get(block.pos.x+dir, block.pos.y));
		if (al != null) {
			for (Block b : al) {
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
					free = false;
					break;
				}
				if (!b.solid) continue;
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockPush && count < 6) {
					((BlockPush)((TypeBlock)b).type).push(dir, count+1);
					free = false;
					pframe = 4;
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
			lastpos.set(block.pos);
			int lastx = (int)block.pos.x;
			int lasty = (int)block.pos.y;
			block.pos.add(dir, 0);
			int newx = (int)block.pos.x;
			int newy = (int)block.pos.y;
			block.layer.move(block, Coord.get(lastx, lasty), Coord.get(newx, newy));
			block.renderoffs.set(lastpos.x-block.pos.x, lastpos.y-block.pos.y, 0f, 0f);
			oframe = 15;
			pframe = 2;
		}
		
		return free;
	}
	
}
