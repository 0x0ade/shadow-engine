package net.fourbytes.shadow.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.Shadow;

public class SnowParticle extends ParticleType {

	public SnowParticle() {
	}

	@Override
	public void create(Particle particle, Color color, float size, float time) {
		size = 0.0625f + 0.1f*Shadow.rand.nextFloat();
		particle.setSize(size, size);
		particle.objgravity = 0.25f*size;
		particle.slowdown = 1f;
		//particle.solid = true;

		particle.time = 30f;
		particle.spawntime = particle.time;

		Color c;

		if (particle.baseColors[particle.imgIDs[0]] == null) {
			c = particle.baseColors[particle.imgIDs[0]] = new Color();
		} else {
			c = particle.baseColors[particle.imgIDs[0]];
		}

		c.set(1f, 1f, 1f, 1f);
		c.mul(0.6f + 0.4f*Shadow.rand.nextFloat());
		c.a = 0.25f + 0.75f*Shadow.rand.nextFloat();
	}

	@Override
	public TextureRegion getTexture(Particle particle, int id) {
		return Images.getTextureRegion("white");
	}

	@Override
	public void tick(Particle particle) {
		super.tick(particle);

		if (particle.time == particle.spawntime-1) {
			particle.movement.add(0f, Shadow.rand.nextFloat() * particle.rec.height * 3f);
		}
	}
	
}
