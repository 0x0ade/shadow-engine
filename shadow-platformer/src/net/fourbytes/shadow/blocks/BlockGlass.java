package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockGlass extends BlockType {

	public TextureRegion tex;

	@IsSaveable
	public int type;
	
	public BlockGlass(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockGlass() {
	}

	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.split("block_glass", 16, 16)[0][type-1] : tex;
	}

	@Override
	public void init() {
		dynamic = false;
		passSunlight = true;
	}
	
}
