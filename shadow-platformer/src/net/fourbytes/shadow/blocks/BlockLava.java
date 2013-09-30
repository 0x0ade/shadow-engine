package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BlockLava extends BlockFluid {
	
	public BlockLava() {
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
