package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;

public abstract class BlockFluid extends BlockType {

	/*Conor-ian water system. Powered by Conor's redstone brain!*/
	public static boolean conor = true;
	@IsSaveable
	public Vector2 sourcepos;

	public float subframe = -1f/60f;
	public int frame = 0;

	public float gframe = 24f/60f;
	public float pframe = 20f/60f;
	
	@IsSaveable
	public int height = 16;

	public boolean topblock = true;
	
	public BlockFluid() {
	}

	@Override
	public void init() {
		tickAlways = true;
		solid = false;
		alpha = 0.75f;
		rendertop = 0x01;
	}
	
	@Override
	public void tick(float delta) {
        if (0f > subframe) {
            subframe += delta * MathUtils.random(0.1f);
            return;
        }

		subframe += delta * MathUtils.random(0.1f);
		if (0f > subframe || subframe > 24f/60f) {
			if (sourcepos != null && conor) {
				if (hasThisFluid(sourcepos)) {
					height-=3;
					if (sourcepos.y < pos.y) {
						height-=3;
					}
				}
				texupdate = true;
			}
			if (height <= 2) {
				layer.remove(this);
			}
		}

		Array<Block> al = layer.get(Coord.get(pos.x, pos.y));
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b == this) {
					continue;
				}
				if (b instanceof BlockFluid || b.solid) {
					layer.remove(this);
					return;
				}
			}
		}
		
		topblock = true;
		al = layer.get(Coord.get(pos.x, pos.y-1));
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b instanceof BlockFluid) {
					height = 16;
					texupdate = true;
					topblock = false;
				}
				if (b.solid) {
					topblock = false;
					break;
				}
			}
		}
		
		gframe -= delta;
		if (gframe <= 0) {
			boolean free = update(0f, 1f, 16, true);
			if (free) {
				gframe = 24f/60f;
			}
		}
		
		pframe -= delta;
		
		boolean onground = false;
		al = layer.get(Coord.get(pos.x, pos.y+1));
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b instanceof BlockFluid) {
					onground = false;
					texupdate = true;
					break;
				}
				if (b.solid) {
					onground = true;
					texupdate = true;
					break;
				}
				//if (!free) break;
			}
		}
		
		if (pframe <= 0 && height > 4 && onground) {
			boolean free1 = update(-1f, 0f, height-1, false);
			boolean free2 = update(1f, 0f, height-1, false);
			if (free1 && free2) {
				pframe = 20f/60f;
			}
		}
		
		rec.height = height/16f;
		colloffs.y = (16-height)/16f;
		colloffs.height = -colloffs.y;
		renderoffs.y = 1f - 1f * (height/16f);
	}
	
	public boolean hasThisFluid(Vector2 pos) {
		Class<? extends BlockFluid> clazz = getClass();
		Array<Block> al = layer.get(Coord.get((int) pos.x, Coord.get1337((int) pos.y))); //TODO Check if 1337 needed for x or not
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b != null && clazz == b.getClass()) {
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
		
		Array<Block> al = layer.get(Coord.get(pos.x+xo, pos.y+yo));
		if (al != null) {
			for (int i = 0; i < al.size; i++) {
				Block b = al.items[i];
				if (b == this) {
					continue;
				}
				if (b instanceof BlockPush) {
					((BlockPush)b).push((int) xo, 0);
				}
				if (b instanceof BlockFluid) {
					if (isSameType(b)) {
						BlockFluid fluid = ((BlockFluid)b);
						if (pos.x != b.pos.x && pos.y != b.pos.y) {
							if (hupdate) {
								fluid.height = height;
								b.texupdate = true;
								if (fluid.sourcepos != null) {
									fluid.sourcepos.set(pos);
								}
							} else {
								if (fluid.height < height) {
									fluid.height = height;
									fluid.texupdate = true;
									if (fluid.sourcepos != null) {
										fluid.sourcepos.set(pos);
									}
								}
							}
						}
						free = false;
						break;
					} else {
						boolean handled = !handleMix((BlockFluid)b);
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
			Block b = create(pos.x + xo, pos.y + yo, height);
			layer.add(b);
		}
		return free;
	}
	
	public Block create(float x, float y, int height) {
		BlockFluid b = (BlockFluid) BlockType.getInstance(subtype, x, y, layer);
		b.tickAlways = true;
		//b.solid = false;
		b.height = height;
		b.sourcepos = new Vector2(pos);
		return b;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		int height = this.height;
		//if (height <= 0) height = 1;
		//if (height > 16) height = 16;
		if (!topblock && height == 16) {
			TextureRegion[][] regs = Images.split(getTexture0(), 16, height);
			if (regs.length > 0) {
				return regs[0][0];
			} else {
				return Images.getTextureRegion("white");
			}
		} else {
			TextureRegion[][] regs = Images.split(getTexture1(), 16, height);
			if (regs.length > 0) {
				return regs[0][frame];
			} else {
				return Images.getTextureRegion("white");
			}
		}
	}

	public abstract TextureRegion getTexture0();
	public abstract TextureRegion getTexture1();

	@Override
	public void frame(float delta) {
		subframe += delta * MathUtils.random(2f/60f);
		if (subframe > 24f/60f) {
			frame++;
			subframe = -1f/60f;
		}
		if (frame >= 4) {
			frame = 0;
			texupdate = true;
		}
	}

	@Override
	public void preRender() {
		super.preRender();
		images[0].setScaleY(-1f * (height/16f));
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
