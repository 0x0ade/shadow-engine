package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;

public class BlockGlass extends BlockType {
	
	public int type;
	
	public BlockGlass(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockGlass() {
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_glass", 16, 16)[0][type-1];
	}

	@Override
	public void init() {
		passSunlight = true;
	}
	
}
