package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlockSignBG extends BlockType {
	
	public int type;
	
	public BlockSignBG(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockSignBG() {
	}

	@Override
	public TextureRegion getTexture() {
		Sprite sheet = new Sprite(Images.getTexture("block_sign_bg"));
		TextureRegion[][] regs = sheet.split(16, 16);
		TextureRegion reg = null;
		reg = regs[0][type-1];
		return reg;
	}
	
}
