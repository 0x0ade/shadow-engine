package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;

public class BlockLab extends BlockType {
	
	public int type;
	
	public BlockLab(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockLab() {
	}

	public void tick() {
		block.blending = false;
	}

	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("block_lab"+type);
	}

}
