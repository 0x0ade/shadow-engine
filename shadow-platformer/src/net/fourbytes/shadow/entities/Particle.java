package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Layer;

public abstract class Particle extends Entity {
	
	public float time = 0;
	public float spawntime = 0;
	public boolean fade = false;
	public static boolean isStatic = false;

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
		tmpimg = getImage();
		if (tmpimg != null) {
			Color c = tmpimg.getColor();
			float af = 0.25f+(3f*(time/spawntime)/4f);
			if (!fade) af = 1f;
			if (oa < 0f) {
				oa = c.a;
			}
			c.a = oa*af;
			tmpimg.setScaleY(-1f);
			//i.setPosition(pos.x * Shadow.dispw/Shadow.vieww, pos.y * Shadow.disph/Shadow.viewh);
			tmpimg.setPosition(pos.x + renderoffs.x, pos.y + rec.height + renderoffs.y);
			tmpimg.setSize(rec.width + renderoffs.width, rec.height + renderoffs.height);
			renderCalc();
		} else {
			System.out.println("I: null; S: "+toString());
		}
	}
	
}
