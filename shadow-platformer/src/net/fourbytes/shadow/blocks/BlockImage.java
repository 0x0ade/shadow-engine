package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;

public class BlockImage extends BlockType {

	public TextureRegion tex;

	public BlockImage(String id) {
		this.attr = new String[] {id};
	}
	
	public BlockImage() {
	}

	@Override
	public void init() {
		dynamic = false;
		blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.getTextureRegion(attr[0]) : tex;
	}
	
}
