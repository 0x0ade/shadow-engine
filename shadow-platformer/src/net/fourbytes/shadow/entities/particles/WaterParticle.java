package net.fourbytes.shadow.entities.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.entities.Particle;
import net.fourbytes.shadow.map.Saveable;

public class WaterParticle extends Particle {

	@Saveable
	public Color color;

	public WaterParticle(Vector2 position, Layer layer, float size, float xdir) {
		super(position, layer, -1);
		setSize(size, size);
		color = new Color(0f, 0.5f, 0.7625f, 0.7f);
		color.mul(1f-(Shadow.rand.nextFloat()/10f));

		objgravity = 0.5f*(8*size);
		movement.x = xdir;
		movement.y = 0.025f;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public void preRender() {
		super.preRender();
		images[0].setColor(tmpc.set(color).mul(tmpcc.set(1f, 1f, 1f, time/spawntime)).mul(layer.tint));
	}

	@Override
	public void collide(GameObject go) {
		if (rec.width/2f <= 0.0775f) {
			layer.remove(this);
			return;
		}
		for (int i = -1; i <= 1; i++) {
			if (MathUtils.randomBoolean(3)) {
				continue;
			}
			WaterParticle rp = new WaterParticle(new Vector2(pos), layer,
					(rec.width/2f)*MathUtils.random(0.5f, 1.25f),
					i*(Shadow.rand.nextFloat()-0.5f)/4f);
			rp.pos.y -= 0.25f;
			rp.movement.y = -rp.rec.height*3f;
			rp.layer.add(rp);
		}
		layer.remove(this);
	}
}
