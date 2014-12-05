package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockWire extends BlockType implements BlockLogic {
	
	@IsSaveable
	public boolean triggered = false;
	@IsSaveable
	public int send = 0;
	
	public BlockWire() {
	}

	@Override
	public void init() {
		tickAlways = true;
		solid = false;
		passSunlight = false;
		alpha = 0f;
		blending = false;
	}

	@Override 
	public void tick(float delta) {
		if (send == -6) {
			for (Layer l : layer.level.layers.values()) {
				for (int xo = -1; xo <= 1; xo++) {
					for (int yo = -1; yo <= 1; yo++) {
						Array<Block> v = l.get(Coord.get(pos.x+xo, pos.y+yo));
						if (v != null) {
							for (int i = 0; i < v.size; i++) {
								Block b = v.items[i];
								if (b instanceof BlockLogic) {
									BlockLogic bl = (BlockLogic) b;
									bl.handle(triggered);
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
	public void preRender() {
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
