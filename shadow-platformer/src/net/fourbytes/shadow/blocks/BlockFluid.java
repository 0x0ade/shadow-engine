package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;

import java.util.Random;

public abstract class BlockFluid extends BlockType {
	
	/*Conor-ian water system. Powered by Conor's redstone brain!*/
	public static boolean conor = true;
	@Saveable
	public Vector2 sourcepos;
	
	int subframe = -1;
	int frame = 0;
	
	int gframe = 24;
	int pframe = 20;
	
	@Saveable
	public int height = 16;
	
	boolean topblock = true;
	
	public BlockFluid() {
	}
	
	public static Random rand = new Random();
	
	@Override
	public void tick() {
		if (subframe == -1) {
			subframe++;
		}
		
		block.interactive = true;
		block.solid = false;
		block.alpha = 0.75f;
		block.rendertop = 0x01;
		
		subframe += rand.nextInt(6);
		block.solid = false;
		if (subframe > 24) {
			frame++;
			subframe = 0;
			if (sourcepos != null && conor) {
				if (hasThisFluid(sourcepos)) {
					height-=3;
					if (sourcepos.y < block.pos.y) {
						height-=3;
					}
				}
			}
			if (height <= 2) {
				block.layer.remove(block);
			}
			block.imgupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			block.pixdur = rand.nextInt(20)+20;
			block.imgupdate = true;
		}
		
		Array<Block> al = block.layer.get(Coord.get(block.pos.x, block.pos.y));
		if (al != null) {
			for (Block b : al) {
				if (b == block) {
					continue;
				}
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
					block.layer.remove(block);
					return;
				}
				if (b.solid) {
					block.layer.remove(block);
					return;
				}
			}
		}
		
		topblock = true;
		al = block.layer.get(Coord.get(block.pos.x, block.pos.y-1));
		if (al != null) {
			for (Block b : al) {
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
					height = 16;
					block.imgupdate = true;
					topblock = false;
				}
				if (b.solid) {
					topblock = false;
					break;
				}
			}
		}
		
		gframe--;
		if (gframe <= 0) {
			boolean free = update(0f, 1f, 16, true);
			if (free) {
				gframe = 24;
			}
		}
		
		pframe--;
		
		boolean onground = false;
		al = block.layer.get(Coord.get(block.pos.x, block.pos.y+1));
		if (al != null) {
			for (Block b : al) {
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
					onground = false;
					block.imgupdate = true;
					break;
				}
				if (b.solid) {
					onground = true;
					block.imgupdate = true;
					break;
				}
				//if (!free) break;
			}
		}
		
		if (pframe <= 0 && height > 4 && onground) {
			boolean free1 = update(-1f, 0f, height-1, false);
			boolean free2 = update(1f, 0f, height-1, false);
			if (free1 && free2) {
				pframe = 20;
			}
		}
		
		block.rec.height = height/16f;
		block.colloffs.y = (16-height)/16f;
		block.colloffs.height = -block.colloffs.y;
		block.renderoffs.y = 1f - 1f * (height/16f);
	}
	
	public boolean hasThisFluid(Vector2 pos) {
		Class clazz = getClass();
		Array<Block> al = block.layer.get(Coord.get((int) pos.x, Coord.get1337((int) pos.y))); //TODO Check if 1337 needed for x or not
		if (al != null) {
			for (Block b : al) {
				if (b != null && b instanceof TypeBlock && clazz == ((TypeBlock)b).type.getClass()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean update(float xo, float yo, int height, boolean hupdate) {
		boolean free = true;
		
		if (height <= 3) {
			return !hupdate;
		}
		
		/*if (conor) {
			height++;
		}*/
		
		Array<Block> al = block.layer.get(Coord.get(block.pos.x+xo, block.pos.y+yo));
		if (al != null) {
			for (Block b : al) {
				if (b == block) {
					continue;
				}
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockPush) {
					((BlockPush)((TypeBlock)b).type).push((int) xo, 0);
				}
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockFluid) {
					if (isSameType(((TypeBlock)b).type)) {
						BlockFluid fluid = ((BlockFluid)((TypeBlock)b).type);
						if (!block.pos.equals(b.pos)) {
							if (hupdate) {
								fluid.height = height;
								b.imgupdate = true;
								if (fluid.sourcepos != null) {
									fluid.sourcepos.set(block.pos);
								}
							} else {
								if (fluid.height < height) {
									fluid.height = height;
									fluid.block.imgupdate = true;
									if (fluid.sourcepos != null) {
										fluid.sourcepos.set(block.pos);
									}
								}
							}
						}
						free = false;
						break;
					} else {
						boolean handled = !handleMix((BlockFluid)((TypeBlock)b).type);
						if (handled) {
							free = false;
							break;
						}
					}
				}
				if (!b.solid) continue;
				free = false;
				break;
				//if (!free) break;
			}
		}
		
		if (free) {
			Block b = create(block.pos.x + xo, block.pos.y + yo, height);
			block.layer.add(b);
		}
		return free;
	}
	
	public Block create(float x, float y, int height) {
		TypeBlock b = (TypeBlock) BlockType.getInstance(subtype, x, y, block.layer);
		b.interactive = true;
		b.solid = false;
		BlockFluid type = (BlockFluid) b.type;
		type.height = height;
		type.sourcepos = new Vector2(block.pos);
		return b;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		int height = this.height;
		//if (height <= 0) height = 1;
		//if (height > 16) height = 16;
		if (!topblock && height == 16) {
			TextureRegion[][] regs = Images.split(getTexture0(), 16, height);
			TextureRegion reg = null;
			if (regs.length > 0) {
				reg = regs[0][0];
				return reg;
			} else {
				return Images.getTextureRegion("white");
			}
		} else {
			TextureRegion[][] regs = Images.split(getTexture1(), 16, height);
			TextureRegion reg = null;
			if (regs.length > 0) {
				reg = regs[0][frame];
				return reg;
			} else {
				return Images.getTextureRegion("white");
			}
		}
	}

	public abstract TextureRegion getTexture0();
	public abstract TextureRegion getTexture1();
	
	@Override
	public void preRender() {
		super.preRender();
		block.images.get(0).setScaleY(-1f * (height/16f));
	}
	
	@Override
	public void render() {
	}
	
	@Override
	public void renderTop() {
		super.render();
	}
	
	boolean isSameType(Object o) {
		return getClass().isInstance(o);
	}
	
	public abstract boolean handleMix(BlockFluid type);
	
	@Override
	public void collide(Entity e) {
		e.movement.scl(0.8f);
		e.movement.y = 0.01f;
		if (e instanceof Player) {
			((Player)e).canJump = ((Player)e).maxJump;

			if (Input.down.isDown) {
				e.movement.y = 0.14f;
			}
			if (Input.up.isDown) {
				e.movement.y = -0.14f;
			}
		}
	}
	
}
