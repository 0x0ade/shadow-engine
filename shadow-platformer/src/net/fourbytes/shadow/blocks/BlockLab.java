package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlockLab extends BlockType {
	
	public int type;
	
	public BlockLab(int type) {
		this.type = type;
		this.attr = new String[] {type+""};
	}
	
	public BlockLab() {
	}

	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("block_lab"+type);
	}
	
	public void tick() {
		block.blending = false;
	}
	
}
