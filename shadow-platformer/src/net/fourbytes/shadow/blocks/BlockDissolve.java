package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.map.Saveable;

public class BlockDissolve extends BlockType implements BlockLogic {
	
	@Saveable
	public boolean triggered = false;
	@Saveable
	public boolean inverted = false;
	
	public BlockDissolve() {
	}
	
	public BlockDissolve(int inverted) {
		this.inverted = inverted == 1;
	}
	
	@Override 
	public void tick() {
		tickAlways = true;
		if (!inverted) {
			solid = !triggered;
		} else {
			solid = triggered;
		}
		passSunlight = !solid;
		alpha = solid?1f:0f;
		blending = false;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_dissolve", 16, 16)[0][0];
	}

	@Override
	public boolean triggered() {
		return triggered;
	}

	@Override
	public void handle(boolean triggered) {
		if (triggered != this.triggered) {
			Sounds.getSound("disappear").play(Sounds.calcVolume(pos), Sounds.calcPitch(1f, 0.15f), 0f);
			this.triggered = triggered;
			
			if ((!inverted && !triggered) || (inverted && triggered)) {
				Array<Block> v = layer.get(Coord.get(pos.x, pos.y));
				for (Block b : v) {
					if (b != this) {
						b.layer.remove(b);
					}
				}
			}
		}
	}

	@Override
	public LogicType getType() {
		return LogicType.INPUT;
	}

	@Override
	public void preRender() {
		if (solid) {
			super.preRender();
		}
	}

	@Override
	public void render() {
		if (solid) {
			super.render();
		}
	}
	
}
