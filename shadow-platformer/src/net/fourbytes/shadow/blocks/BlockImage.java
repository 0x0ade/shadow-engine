package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;

public class BlockImage extends BlockType {
	
	public BlockImage(String id) {
		this.attr = new String[] {id};
	}
	
	public BlockImage() {
	}

	@Override
	public void tick() {
		block.blending = false;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion(attr[0]);
	}
	
}
