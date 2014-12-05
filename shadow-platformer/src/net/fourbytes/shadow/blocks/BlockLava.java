package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;

public class BlockLava extends BlockFluid {

	public float lightDelta = 1f/5f;

	public BlockLava() {
	}

	@Override
	public void tick(float delta) {
		light.set(1f, 0.35f, 0.01f, 1f);
		light.add(MathUtils.random(lightDelta), MathUtils.random(lightDelta), MathUtils.random(lightDelta), 0f);
		tintSunlight.set(light);
		super.tick(delta);
	}

	@Override
	public TextureRegion getTexture0() {
		return Images.getTextureRegion("block_lava");
	}
	
	@Override
	public TextureRegion getTexture1() {
		return Images.getTextureRegion("block_lava_top");
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		e.hurt(null, 0.002f);
	}
	
	@Override
	public boolean handleMix(BlockFluid type) {
		if (type instanceof BlockWater) {
			//Generate Obsidian! TRANSFORMATION!
			BlockType newblock = BlockType.getInstance("BlockObsidian", pos.x, pos.y, layer);
			layer.add(newblock);
			layer.remove(this);
			return false;
		}
		return true;
	}
	
}
