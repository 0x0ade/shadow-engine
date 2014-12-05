package net.fourbytes.shadow.weathers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.systems.Weather;

public class RainWeather extends Weather {

	public float time = 0f;
	public float direction = 0f;

	public RainWeather(Level level) {
		this(level, MathUtils.random(-0.15f, 0.15f));
	}

	public RainWeather(Level level, float direction) {
		super(level);
		this.direction = direction;
	}

	@Override
	public void start() {
		//TODO set up ambient / environmental lighting
	}

	@Override
	public void tick(float delta) {
		if (time <= 0) {
			Rectangle vp = Shadow.cam.camrec;
			Particle rp = level.systems.get(IParticleManager.class).create("RainParticle", new Vector2(MathUtils.random(vp.x - 10f, vp.x + vp.width + 10f), vp.y - 2f),
					level.player.layer, null, 0f, 0f);
			rp.movement.x = direction;
			rp.layer.add(rp);
			time = MathUtils.random(1f, 2f);
		}
		time -= delta*60f;
	}

	@Override
	public void stop() {
		//TODO reset ambient / environmental lighting
	}

}
