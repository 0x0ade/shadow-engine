package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.fourbytes.shadow.Images;

public class BlockWater extends BlockFluid {

	public float lightDelta = 1f/5f;

	public BlockWater() {
	}

	@Override
	public void init() {
		passSunlight = true;
	}

	@Override
	public void tick() {
		tintSunlight.set(0f, 0.5f, 0.7625f, 1f);
		tintSunlight.add(MathUtils.random(lightDelta), MathUtils.random(lightDelta), MathUtils.random(lightDelta), 0f);
		super.tick();
	}
	
	@Override
	public TextureRegion getTexture0() {
		return Images.getTextureRegion("block_water");
	}
	
	@Override
	public TextureRegion getTexture1() {
		return Images.getTextureRegion("block_water_top");
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
