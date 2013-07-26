package net.fourbytes.shadow;

import java.util.Vector;

import com.badlogic.gdx.graphics.Color;

public class NoLightEngine extends LightEngine {
	
	public NoLightEngine(Level level) {
		super(level);
	}

	@Override
	public void setLight(Block b, Layer ll) {
		setLight((GameObject) b, ll);
	}

	@Override
	public void setLight(Entity e, Layer ll) {
		setLight((GameObject) e, ll);
	}
	
	public void setLight(GameObject go, Layer ll) {
		go.lightTint.set(1f, 1f, 1f, 1f);
		go.cantint = true;
	}

}
