package net.fourbytes.shadow.systems;

import net.fourbytes.shadow.Level;

public abstract class Weather {

	public Level level;

	public Weather(Level level) {
		this.level = level;
	}

	public abstract void start();
	public abstract void tick(float delta);
	public abstract void stop();

}
