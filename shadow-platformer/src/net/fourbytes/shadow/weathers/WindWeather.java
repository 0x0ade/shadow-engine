package net.fourbytes.shadow.weathers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.systems.Weather;

public class WindWeather extends Weather {

	protected float xgravold = 0f;
	public float time = 0f;
	public float direction = 0f;

	public WindWeather(Level level) {
		this(level, MathUtils.random(-0.05f, 0.05f));
	}

	public WindWeather(Level level, float direction) {
		super(level);
		this.direction = direction;
	}

	@Override
	public void start() {
		//TODO set up ambient / environmental lighting
		xgravold = level.xgravity;
		level.xgravity = direction;
	}

	@Override
	public void tick(float delta) {
		if (time == 0) {
			Rectangle vp = Shadow.cam.camrec;
			Particle wp = level.systems.get(IParticleManager.class).create("WindParticle", new Vector2(vp.x + (direction > 0f ? 0.1f : (vp.width + 0.1f)), MathUtils.random(vp.y - 10f, vp.y + vp.height + 10f)),
					level.player.layer, null, 0f, 0f);
			wp.movement.x = direction * 10f;
			wp.layer.add(wp);
            time = MathUtils.random(1f, 2f);
		}
		time -= delta*60f;
	}

	@Override
	public void stop() {
		//TODO reset ambient / environmental lighting
		level.xgravity = xgravold;
	}

}
