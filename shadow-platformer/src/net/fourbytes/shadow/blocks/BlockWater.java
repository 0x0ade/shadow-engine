package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BlockWater extends BlockFluid {
	
	public BlockWater() {
	}
	
	@Override
	public void tick() {
		block.interactive = true;
		block.passSunlight = true;
		block.tintSunlight.set(0f, 0.5f, 0.7625f, 1f);
		float ff = 5f;
		block.tintSunlight.add((1f/ff)-((float)Math.random())/ff, (1f/ff)-((float)Math.random())/ff, (1f/ff)-((float)Math.random())/ff, 0f);
		super.tick();
	}
	
	@Override
	public Texture getTexture0() {
		return Images.getTexture("block_water");
	}
	
	@Override
	public Texture getTexture1() {
		return Images.getTexture("block_water_top");
	}
	
	
	@Override
	public boolean handleMix(BlockFluid type) {
		if (type instanceof BlockLava) {
			//Lava does that for meh, I think 
			type.handleMix(this);
			return false;
		}
		return true;
	}
	
}
