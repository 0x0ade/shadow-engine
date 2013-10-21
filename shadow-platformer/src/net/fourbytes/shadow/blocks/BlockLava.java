package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;

public class BlockLava extends BlockFluid {
	
	public BlockLava() {
	}

	@Override
	public void tick() {
		block.light.set(1f, 0.35f, 0.01f, 1f);
		float ff = 5f;
		block.light.add((1f/ff)-((float)Math.random())/ff, (1f/ff)-((float)Math.random())/ff, (1f/ff)-((float)Math.random())/ff, 0f);
		super.tick();
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
			Block newblock = BlockType.getInstance("BlockObsidian", block.pos.x, block.pos.y, block.layer);
			block.layer.add(newblock);
			block.layer.remove(block);
			return false;
		}
		return true;
	}
	
}
