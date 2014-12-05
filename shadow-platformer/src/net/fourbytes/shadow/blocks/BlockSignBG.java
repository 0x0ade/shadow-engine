package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockSignBG extends BlockType {

	public TextureRegion tex;

	@IsSaveable
	public int type;
	
	public BlockSignBG(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockSignBG() {
	}

	@Override
	public void init() {
		dynamic = false;
		blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.split("block_sign_bg", 16, 16)[0][type-1] : tex;
	}
	
}
