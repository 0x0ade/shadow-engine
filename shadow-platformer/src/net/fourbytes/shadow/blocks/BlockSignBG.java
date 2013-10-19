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

	public void tick() {
		block.blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_sign_bg", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][type-1];
		return reg;
	}
	
}
