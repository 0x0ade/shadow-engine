package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockClock extends BlockType implements BlockLogic {

	@IsSaveable
	public boolean triggered = false;
	@IsSaveable
	public float cycle = 128f/60f;
	@IsSaveable
	public float time = 0f;
	
	public BlockClock() {
	}


	public BlockClock(int time) {
		this.time = time*32f/60f;
	}

    public BlockClock(float time, float cycle) {
        this.time = time;
        this.cycle = cycle;
    }

	@Override
	public void init() {
		tickAlways = true;
		solid = false;
		alpha = 0f;
		passSunlight = false;
		blending = false;
	}

	@Override 
	public void tick(float delta) {
		time += delta;
		if (time >= cycle) {
			time = 0f;
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
