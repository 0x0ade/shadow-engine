package net.fourbytes.shadow.weathers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.systems.Weather;

public class SnowWeather extends Weather {

	public float direction = 0f;

	public SnowWeather(Level level) {
		this(level, MathUtils.random(-0.1f, 0.1f));
	}

	public SnowWeather(Level level, float direction) {
		super(level);
		this.direction = direction;
	}

	@Override
	public void start() {
		//TODO set up ambient / environmental lighting
	}

	@Override
	public void tick(float delta) {
		Rectangle vp = Shadow.cam.camrec;
		Particle sp = level.systems.get(IParticleManager.class).create("SnowParticle", new Vector2(MathUtils.random(vp.x - 20f, vp.x + vp.width + 20f), vp.y - 2f),
				level.player.layer, null, 0f, 0f);
		sp.movement.x = direction;
		sp.layer.add(sp);
	}

	@Override
	public void stop() {
		//TODO reset ambient / environmental lighting
	}

}
