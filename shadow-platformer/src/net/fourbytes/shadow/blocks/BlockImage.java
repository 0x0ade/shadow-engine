package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
	public TextureRegion getTexture() {
		return Images.getTextureRegion(attr[0]);
	}
	
}
