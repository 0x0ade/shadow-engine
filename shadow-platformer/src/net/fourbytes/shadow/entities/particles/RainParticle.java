package net.fourbytes.shadow.entities.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.entities.Particle;

public class RainParticle extends Particle {

	public Color color;

	public RainParticle(Vector2 position, Layer layer, float size, float xdir) {
		super(position, layer, -1);
		setSize(size, size);
		color = new Color(0f, 0.5f, 0.7625f, 0.7f);
		color.mul(1f-(Shadow.rand.nextFloat()/10f));

		objgravity = 0.5f*(8*size);
		movement.x = xdir;
		movement.y = 0.025f;
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void preRender() {
		super.preRender();
		tmpimg.setColor(tmpc.set(color).mul(tmpcc.set(1f, 1f, 1f, time/spawntime)).mul(layer.tint));
	}

	@Override
	public void collide(GameObject go) {
		if (rec.width/2f <= 0.0775f) {
			layer.remove(this);
			return;
		}
		//FIXME
		for (int i = 0; i < 2; i++) {
			RainParticle rp = new RainParticle(new Vector2(pos), layer, rec.width/2f, (Shadow.rand.nextFloat()-0.5f)/4f);
			rp.pos.y -= 0.25f;
			rp.movement.y = -0.05f;
			rp.layer.add(rp);
		}
		layer.remove(this);
	}
}
