package net.fourbytes.shadow.systems;

/**
 * A WeatherSystem manages the weather of the level, calling the weather's
 * tick method and changing the weather when needed.
 */
public interface IWeatherSystem extends ISystem, ITickable {
	public void register(Class<? extends Weather> weather, float probability);
    public String getWeather();
    public void setWeather(String name);
}
