package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Layer;

public class MobTest extends Mob {

	public MobTest(Vector2 position, Layer layer) {
		super(position, layer);
		maxframe = 4;
		alpha = 0.5f;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("player", 16, 16)[facingLeft?0:1][frame];
	}
	
	@Override
	public void tint(int id, Image img) {
		super.tint(id, img);
		img.getColor().mul(0f, 0f, 0f, 1f);
	}
	
}
