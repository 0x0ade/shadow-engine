package net.fourbytes.shadow.systems;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.weathers.RainWeather;
import net.fourbytes.shadow.weathers.SnowWeather;
import net.fourbytes.shadow.weathers.WindWeather;

import java.lang.reflect.InvocationTargetException;

public class DefaultWeatherSystem implements IWeatherSystem, INextDay {

	public Array<Class<? extends Weather>> weathers = new Array<Class<? extends Weather>>(Class.class);
    public ObjectMap<String, Class<? extends Weather>> weatherMap = new ObjectMap<String, Class<? extends Weather>>();
	public ObjectFloatMap<Class<? extends Weather>> probabilities = new ObjectFloatMap<Class<? extends Weather>>();

	public Level level;

	public Weather current;

	public DefaultWeatherSystem(Level level) {
		this.level = level;
		register(RainWeather.class, 0.4f);
		register(WindWeather.class, 0.1f);
		register(SnowWeather.class, 0.2f);
	}

	@Override
	public void register(Class<? extends Weather> weather, float probability) {
		weathers.add(weather);
        weatherMap.put(weather.getSimpleName(), weather);
		probabilities.put(weather, probability);
	}

    @Override
    public String getWeather() {
        return current.getClass().getSimpleName();
    }

    @Override
    public void setWeather(String name) {
        Weather previous = current;
        current = null;

        Class<? extends Weather> clazz = weatherMap.get(name);

        if (clazz == null) {
            if (previous != null) {
                previous.stop();
            }
            return;
        }

        try {
            current = clazz.getConstructor(Level.class).newInstance(level);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (previous != null) {
            previous.stop();
        }

        if (current != null) {
            current.start();
        }

    }

    @Override
	public void tick(float delta) {
		if (current != null) {
			current.tick(delta);
		}
	}

	@Override
	public void nextDay() {
		Weather previous = current;
		current = null;

		for (ObjectFloatMap.Entry<Class<? extends Weather>> entry : probabilities.entries()) {
			if (Shadow.rand.nextFloat() <= entry.value) {
				try {
					current = entry.key.getConstructor(Level.class).newInstance(level);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (previous != null) {
			previous.stop();
		}

		if (current != null) {
			current.start();
		}
	}

    @Override
    public String getName() {
        return "WeatherSystem";
    }

}
