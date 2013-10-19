package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.blocks.BlockType;

public class TypeBlock extends Block {
	
	public BlockType type;
	
	public TypeBlock(Vector2 pos, Layer layer, BlockType type) {
		super(pos, layer);
		this.type = type;
		this.type.block = this;
	}
	
	@Override
	public Image getImage(int id) {
		return type.getImage(id);
	}

	public Image superGetImage(int id) {
		return super.getImage(id);
	}

	@Override
	public TextureRegion getTexture(int id) {
		return type.getTexture(id);
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
	}

	public void superPreRender() {
		super.preRender();
	}
	
	@Override
	public void render() {
		type.render();
	}

	public void superRender() {
		super.render();
	}
	
	@Override
	public void renderTop() {
		type.renderTop();
	}
	
}
