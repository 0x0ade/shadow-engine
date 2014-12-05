package net.fourbytes.shadow.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.mod.ModManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DefaultParticleManager implements IParticleManager {

	public Level level;

	public final ObjectMap<String, ParticleType> types = new ObjectMap<String, ParticleType>();
	private final Array<Particle> particles = new Array<Particle>(Particle.class);

	public DefaultParticleManager(Level level) {
		this.level = level;
	}

	@Override
	public Particle create(String typeName, Vector2 pos, Layer layer, Color color, float size, float time, Object... args) {
		ParticleType type = ModManager.getParticleType(typeName);

		if (type == null) {
			try {
				Class<ParticleType> clazz = ClassReflection.forName("net.fourbytes.shadow.particles."+typeName);
				Constructor<ParticleType> constructor = clazz.getConstructor();
				constructor.setAccessible(true);
				type = constructor.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!types.containsKey(typeName)) {
			types.put(typeName, type);
		}

		return create(type, pos, layer, color, size, time, args);
	}

	@Override
	public Particle create(ParticleType type, Vector2 pos, Layer layer, Color color, float size, float time, Object... args) {
		Class<? extends ParticleType> typeClazz = type.getClass();

		String typeName = typeClazz.getSimpleName();
		if (!types.containsKey(typeName)) {
			types.put(typeName, type);
		}

		Particle particle;

		if (particles.size == 0) {
			particle = new Particle();
		} else {
			particle = particles.removeIndex(0);
		}

		particle.create(pos, layer, type);

		if (args.length == 0) {
			type.create(particle, color, size, time);
		} else {
			try {
				Object[] allargs = new Object[args.length+4];
				allargs[0] = particle;
				allargs[1] = color;
				allargs[2] = size;
				allargs[3] = time;
				System.arraycopy(args, 0, allargs, 4, args.length);

				Class[] types = new Class[args.length+4];
				types[0] = Particle.class;
				types[1] = Color.class;
				types[2] = float.class;
				types[3] = float.class;
				for (int i = 0; i < args.length; i++) {
					types[i+4] = args[i].getClass();
				}

				Method method = typeClazz.getMethod("create", types);
				method.setAccessible(true);
				method.invoke(type, allargs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return particle;
	}

	@Override
	public void reset(Particle particle) {
		particle.type.reset(particle);
		particle.reset();
		particles.add(particle);
	}

    @Override
    public String getName(ParticleType type) {
        return type.getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return "ParticleManager";
    }

}
