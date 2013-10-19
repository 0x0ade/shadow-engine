package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;

public class BlockClock extends BlockType implements BlockLogic {
	
	boolean triggered = false;
	int factor = 32;
	int totime = 4;
	int timer = 0;
	
	public BlockClock() {
	}
	
	public BlockClock(int timer) {
		this.timer = timer*factor;
	}
	
	@Override 
	public void tick() {
		block.interactive = true;
		block.solid = false;
		block.alpha = 0f;
		block.passSunlight = false;
		block.blending = false;
		timer++;
		if (timer >= totime*factor) {
			timer = 0;
			triggered = !triggered;
			
			for (Layer l : block.layer.level.layers.values()) {
				for (int xo = -1; xo <= 1; xo++) {
					for (int yo = -1; yo <= 1; yo++) {
						Array<Block> v = l.get(Coord.get(block.pos.x + xo, block.pos.y + yo));
						if (v != null) {
							for (Block b : v) {
								if (b instanceof TypeBlock) {
									TypeBlock tb = (TypeBlock) b;
									BlockType type = tb.type;
									if (type instanceof BlockLogic) {
										BlockLogic bl = (BlockLogic) type;
										bl.handle(triggered);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_clock", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][0];
		return reg;
	}
	
	@Override
	public void render() {
	}
	
	@Override
	public boolean triggered() {
		return triggered;
	}

	@Override
	public void handle(boolean triggered) {
		//this.triggered = triggered;
	}

	@Override
	public LogicType getType() {
		return LogicType.OUTPUT;
	}
	
}
