package net.fourbytes.shadow;

import net.fourbytes.shadow.blocks.BlockType;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TypeBlock extends Block {
	
	public BlockType type;
	
	public TypeBlock(Vector2 pos, Layer layer, BlockType type) {
		super(pos, layer);
		this.type = type;
		this.type.block = this;
	}
	
	@Override
	public Image getImage() {
		return type.getImage();
	}
	
	@Override
	public TextureRegion getTexture() {
		return type.getTexture();
	}
	
	@Override
	public void tick() {
		super.tick();
		type.tick();
	}
	
	@Override
	public void collide(Entity e) {
		type.collide(e);
	}
	
	@Override
	public void preRender() {
		type.preRender();
		if (tmpimg != null) {
			renderCalc();
		}
	}
	
	@Override
	public void render() {
		type.render();
	}
	
}
