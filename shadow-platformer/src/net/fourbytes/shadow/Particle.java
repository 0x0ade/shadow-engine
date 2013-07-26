package net.fourbytes.shadow;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public abstract class Particle extends Entity {
	
	float time = 0;
	float spawntime = 0;
	boolean fade = false;
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
		time--;
	}
	
	float oa = -1f;
	
	@Override
	public void preRender() {
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
