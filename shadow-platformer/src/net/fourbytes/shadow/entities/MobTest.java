package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Layer;

public class MobTest extends Mob {

	public MobTest(Vector2 position, Layer layer) {
		super(position, layer);
		maxframe = 4;
		alpha = 0.5f;
	}

	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = Images.split("player", 16, 16);
		TextureRegion reg = null;
		reg = regs[facingLeft?0:1][frame];
		return reg;
	}
	
	@Override
	public void tint() {
		super.tint();
		tmpimg.getColor().mul(0f, 0f, 0f, 1f);
	}
	
}
