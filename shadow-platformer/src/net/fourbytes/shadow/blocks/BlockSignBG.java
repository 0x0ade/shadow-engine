package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;

public class BlockSignBG extends BlockType {
	
	public int type;
	
	public BlockSignBG(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockSignBG() {
	}

	@Override
	public void init() {
		blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_sign_bg", 16, 16)[0][type-1];
	}
	
}
