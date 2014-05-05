package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.map.Saveable;

public class Block extends GameObject {
	
	public String subtype = "";
	@Saveable
	public boolean tickInView = false;
	@Saveable
	public boolean tickAlways = false;
	/**
	 * 0x00 = normal only, 0x01 = rendertop only, anything other = both
	 */
	@Saveable
	public byte rendertop = 0x00;
	@Saveable
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
