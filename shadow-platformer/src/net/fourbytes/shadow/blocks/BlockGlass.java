package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlockGlass extends BlockType {
	
	public int type;
	
	public BlockGlass(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockGlass() {
	}

	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = Images.split("block_glass", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][type-1];
		return reg;
	}
	
	public void tick() {
		super.tick();
		block.passSunlight = true;
	}
	
}
