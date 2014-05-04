package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block extends GameObject {
	
	public String subtype = "";
	public boolean tickInView = false;
	public boolean tickAlways = false;
	/**
	 * 0x00 = normal only, 0x01 = rendertop only, anything other = both
	 */
	public byte rendertop = 0x00;
	public Rectangle colloffs = new Rectangle(0, 0, 0, 0);
	
	public Block(Vector2 pos, Layer layer) {
		super(pos, layer);
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_test");
	}

	@Override
	public void tick() {
		super.tick();
	}
	
	public void collide(Entity e) {
	}
	
	public void renderTop() {
	}
}
