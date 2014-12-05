package net.fourbytes.shadow.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.Shadow;

public class PixelParticle extends ParticleType {

	public PixelParticle() {
	}

    @Override
    public void create(Particle particle, Color color, float size, float time) {
        super.create(particle, color, size, time);

        particle.movement.x = (Shadow.rand.nextFloat()-0.5f)*particle.rec.width * 2f;
        particle.movement.y = (Shadow.rand.nextFloat()-0.5f)*particle.rec.height * 3f;
    }

	@Override
	public TextureRegion getTexture(Particle particle, int id) {
		return Images.getTextureRegion("white");
	}

	@Override
	public void tick(Particle particle) {
		super.tick(particle);
	}
	
}
