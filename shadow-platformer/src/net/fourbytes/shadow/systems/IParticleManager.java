package net.fourbytes.shadow.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;

/**
 * The ParticleManager (previously ParticlePool) manages the re-use of particle object
 * instances to increase overall performance by decreasing the number of garbage
 * collection calls (also known as pooling).
 */
public interface IParticleManager extends ISystem {
	public Particle create(String typeName, Vector2 pos, Layer layer, Color color, float size, float time, Object... args);
	public Particle create(ParticleType type, Vector2 pos, Layer layer, Color color, float size, float time, Object... args);

	public void reset(Particle particle);

    public String getName(ParticleType type);
}
