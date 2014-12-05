package net.fourbytes.shadow.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.Shadow;

public class WindParticle extends ParticleType {

	public WindParticle() {
	}

	@Override
	public void create(Particle particle, Color color, float size, float time) {
		size = 0.0625f + 0.05f*Shadow.rand.nextFloat();
		particle.setSize(size, size);
		particle.objgravity = 0f;
		particle.slowdown = 0f;
		//particle.solid = true;

		particle.time = 4f;
		particle.spawntime = particle.time;

		Color c;

		if (particle.baseColors[particle.imgIDs[0]] == null) {
			c = particle.baseColors[particle.imgIDs[0]] = new Color();
		} else {
			c = particle.baseColors[particle.imgIDs[0]];
		}

		c.set(1f, 1f, 1f, 1f);
		c.mul(0.25f + 0.6f*Shadow.rand.nextFloat());
		c.a = 0.2f + 0.4f*Shadow.rand.nextFloat();
	}

	@Override
	public TextureRegion getTexture(Particle particle, int id) {
		return Images.getTextureRegion("white");
	}

	@Override
	public void tick(Particle particle) {
		super.tick(particle);

		if (particle.time == particle.spawntime-1) {
			particle.movement.set(0f, Shadow.rand.nextFloat() * particle.rec.height * 3f);
		}
	}
	
}
