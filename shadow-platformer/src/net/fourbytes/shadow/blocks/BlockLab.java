package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockLab extends BlockType {

	public TextureRegion tex;

	@IsSaveable
	public int type;
	
	public BlockLab(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockLab() {
	}

	@Override
	public void init() {
		dynamic = false;
		blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.getTextureRegion("block_lab"+type) : tex;
	}

}
