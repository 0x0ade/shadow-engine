package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.map.Saveable;

public class BlockLab extends BlockType {

	public TextureRegion tex;

	@Saveable
	public int type;
	
	public BlockLab(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockLab() {
	}

	@Override
	public void init() {
		blending = false;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.getTextureRegion("block_lab"+type) : tex;
	}

}
