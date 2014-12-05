package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public abstract class ParticleType {

	public ParticleType() {
	}

	public void create(Particle particle, Color color, float size, float time) {
		if (size == 0) {
			size = 0.0625f + 0.05f*Shadow.rand.nextFloat();
		}
		particle.setSize(size, size);
		particle.objgravity = 0.5f*(8f*size);

		if (time == 0) {
			particle.time = MathUtils.random(0.25f) + 0.25f;
		} else {
			particle.time = time;
		}
		particle.spawntime = particle.time;

		if (particle.baseColors[particle.imgIDs[0]] == null) {
            if (color != null) {
                particle.baseColors[particle.imgIDs[0]] = new Color(color);
            } else {
                particle.baseColors[particle.imgIDs[0]] = new Color(1f, 1f, 1f, 1f);
            }
        } else {
            if (color != null) {
                particle.baseColors[particle.imgIDs[0]].set(color);
            } else {
                particle.baseColors[particle.imgIDs[0]].set(1f, 1f, 1f, 1f);
            }
		}

		//particle.solid = false;
	}

	public void reset(Particle particle) {
	}

	public void tick(Particle particle) {
	}

	public int[] getTextureIds(Particle particle) {
		return new int[] {0};
	}

	public abstract TextureRegion getTexture(Particle particle, int id);
}
