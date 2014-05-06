package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.map.Saveable;

public abstract class Particle extends Entity {

	@Saveable
	public float time = 0;
	@Saveable
	public float spawntime = 0;
	@Saveable
	public boolean fade = false;
	@Saveable
	public boolean interactive = false;

	public Particle(Vector2 position, Layer layer, int time) {
		super(position, layer);
		this.time = time;
		this.spawntime = time;
		//this.solid = false;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (time == 0) {
			layer.remove(this);
			return;
		}
		if (time > 0) {
			time--;
		}
	}
	
	protected float oa = -1f;
	protected Color tmpc = new Color();
	protected Color tmpcc = new Color();
	
	@Override
	public void preRender() {
		super.preRender();
	}

	@Override
	public void tint(int id, Image img) {
		super.tint(id, img);
		Color c = img.getColor();
		float af = 0.25f+(3f*(time/spawntime)/4f);
		if (!fade) af = 1f;
		if (oa < 0f) {
			oa = c.a;
		}
		c.a = oa*af;
	}
	
}
