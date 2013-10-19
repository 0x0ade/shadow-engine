package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.Saveable;

public class BlockWire extends BlockType implements BlockLogic {
	
	@Saveable
	boolean triggered = false;
	@Saveable
	int send = 0;
	
	public BlockWire() {
	}
	
	@Override 
	public void tick() {
		block.interactive = true;
		block.solid = false;
		block.passSunlight = false;
		block.alpha = 0f;
		block.blending = false;
		
		if (send == -6) {
			for (Layer l : block.layer.level.layers.values()) {
				for (int xo = -1; xo <= 1; xo++) {
					for (int yo = -1; yo <= 1; yo++) {
						Array<Block> v = l.get(Coord.get(block.pos.x+xo, block.pos.y+yo));
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
		send++;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_wire");
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
		if (send > 0) {
			this.triggered = triggered;
			send = -6;
		}
	}

	@Override
	public LogicType getType() {
		return LogicType.PUTPUT;
	}
	
}
