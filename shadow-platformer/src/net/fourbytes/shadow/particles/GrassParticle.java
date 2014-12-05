package net.fourbytes.shadow.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.Shadow;

public class GrassParticle extends ParticleType {

	public GrassParticle() {
	}

	@Override
	public void create(Particle particle, Color color, float size, float time) {
		size = 0.0625f + 0.05f*Shadow.rand.nextFloat();
		particle.setSize(size, size);
		particle.objgravity = Shadow.rand.nextFloat()-0.4f;

		particle.time = 6f + MathUtils.random(3f);
		particle.spawntime = particle.time;

		particle.light.set(0f, 0.4f + 0.5f*Shadow.rand.nextFloat(), 0.225f + 0.2f*Shadow.rand.nextFloat(), 1f);
		particle.light.mul(0.4f);
		particle.light.a = 0.0775f;

		if (particle.baseColors[particle.imgIDs[0]] == null) {
			particle.baseColors[particle.imgIDs[0]] = new Color(particle.light);
		} else {
			particle.baseColors[particle.imgIDs[0]].set(particle.light);
		}

		particle.solid = false;
	}
	
	@Override
	public TextureRegion getTexture(Particle particle, int id) {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void tick(Particle particle) {
		super.tick(particle);

		particle.light.set(particle.baseColors[particle.imgIDs[0]]);
		particle.light.mul(0.4f);
		particle.light.a = 0.0775f;
		particle.light.mul(particle.time / particle.spawntime);
		particle.imgupdate = true;

		particle.movement.x = (Shadow.rand.nextFloat()-0.5f)*particle.rec.width * 2f;
		particle.movement.y = (Shadow.rand.nextFloat()-0.5f)*particle.rec.height * 0.75f;
	}

}
