package net.fourbytes.shadow.systems;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.mod.ModManager;
import net.fourbytes.shadow.utils.Cache;

public class DefaultSystemManager implements ISystemManager {

	protected final Cache<Array<ISystem>> cache = new Cache(Array.class, 64,
			new Object[] {false, 16, ISystem.class}, new Class[] {boolean.class, int.class, Class.class});
	protected final ObjectMap<Class<?>, Object[]> gotAll = new ObjectMap<Class<?>, Object[]>();

	public Level level;
	public ObjectMap<String, ISystem> systemmap = new ObjectMap<String, ISystem>();
	public ObjectMap<Class<? extends ISystem>, ISystem> isystemmap = new ObjectMap<Class<? extends ISystem>, ISystem>();
	public Array<ISystem> systems = new Array<ISystem>(ISystem.class);

	public DefaultSystemManager(Level level) {
		this.level = level;
	}

	@Override
	public void init() {
		init("ParticleManager");
		init("LightSystem");
		init("WeatherSystem");
		init("TimeDaySystem");
        init("MusicSystem");
		ModManager.initLevelSystems(level);
	}

	@Override
	public void init(String name) {
		if (systemmap.containsKey(name)) {
			return;
		}

		ISystem system = ModManager.initLevelSystem(level, name);
		if (system == null) {
			try {
				Class<? extends ISystem> clazz = ClassReflection.forName("net.fourbytes.shadow.systems.Default"+name);
				system = clazz.getConstructor(Level.class).newInstance(level);
			} catch (Exception e) {
				//System not found or other error - ignore.
			}
        }

        if (system == null || !name.equals(system.getName())) {
            return;
        }

		set(name, system);
	}

	@Override
	public void set(String name, ISystem system) {
		if (system == null) {
			return;
		}

		ISystem systemPrevious = systemmap.get(name);
		if (systemPrevious != null) {
			systems.removeValue(systemPrevious, true);
		}

		systems.add(system);
		systemmap.put(name, system);

		Class clazzISystem = ISystem.class;
		Class clazz = system.getClass();
		Class<?>[] clazzInterfaces = clazz.getInterfaces();
		for (Class<?> i : clazzInterfaces) {
			if (clazzISystem.isAssignableFrom(i)) {
				isystemmap.put((Class<? extends ISystem>) i, system);
			}
		}

		gotAll.clear();
	}

	@Override
	public ISystem get(String name) {
		return systemmap.get(name);
	}

	@Override
	public <T> T get(Class<T> isystem) {
		return (T) isystemmap.get((Class<? extends ISystem>) isystem);
	}

	@Override
	public <T> T[] getAll(Class<T> clazz) {
		Object[] got = gotAll.get(clazz);
		if (got != null) {
			return (T[]) got;
		}

		Array<ISystem> matching = cache.getNext();
		matching.clear();
		for (int i = 0; i < systems.size; i++) {
			ISystem system = systems.items[i];
			if (clazz.isAssignableFrom(system.getClass())) {
				matching.add(system);
			}
		}
		T[] array = (T[]) ArrayReflection.newInstance(clazz, matching.size);
		System.arraycopy(matching.items, 0, array, 0, array.length);
		gotAll.put(clazz, array);
		return array;
	}

    @Override
    public ISystem[] getAll() {
        return systems.toArray();
    }

	@Override
	public void tick(float delta) {
		ITickable[] tickables = getAll(ITickable.class);
		for (int i = 0; i < tickables.length; i++) {
			ITickable tickable = tickables[i];
			tickable.tick(delta);
		}
	}

    @Override
    public String getName() {
        return null;
    }
}
