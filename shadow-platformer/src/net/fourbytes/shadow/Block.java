package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.map.IsSaveable;

public class Block extends GameObject {
	
	public String subtype = "";
	@IsSaveable
	public boolean tickInView = false;
	@IsSaveable
	public boolean tickAlways = false;
	/**
	 * 0x00 = normal only, 0x01 = rendertop only, anything other = both
	 */
	@IsSaveable
	public byte rendertop = 0x00;
	@IsSaveable
	public Rectangle colloffs = new Rectangle(0, 0, 0, 0);
	@IsSaveable
	public boolean dynamic = true;//TODO set properly

	public Block(Vector2 pos, Layer layer) {
		super(pos, layer);
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_test");
	}

	public void collide(Entity e) {
	}
	
	public void renderTop() {
	}
}
