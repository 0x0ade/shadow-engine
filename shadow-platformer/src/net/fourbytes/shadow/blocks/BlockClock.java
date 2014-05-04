package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.Saveable;

public class BlockClock extends BlockType implements BlockLogic {

	@Saveable
	public boolean triggered = false;
	@Saveable
	public int factor = 32;
	@Saveable
	public int totime = 4;
	@Saveable
	public int timer = 0;
	
	public BlockClock() {
	}
	
	public BlockClock(int timer) {
		this.timer = timer*factor;
	}
	
	@Override 
	public void tick() {
		tickAlways = true;
		solid = false;
		alpha = 0f;
		passSunlight = false;
		blending = false;
		timer++;
		if (timer >= totime*factor) {
			timer = 0;
			triggered = !triggered;
			
			for (Layer l : layer.level.layers.values()) {
				for (int xo = -1; xo <= 1; xo++) {
					for (int yo = -1; yo <= 1; yo++) {
						Array<Block> v = l.get(Coord.get(pos.x + xo, pos.y + yo));
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
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_clock", 16, 16)[0][0];
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
		//this.triggered = triggered;
	}

	@Override
	public LogicType getType() {
		return LogicType.OUTPUT;
	}
	
}
