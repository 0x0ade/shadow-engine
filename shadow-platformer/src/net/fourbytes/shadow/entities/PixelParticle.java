package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Layer;

import java.util.Random;

public class PixelParticle extends Particle {
	
	Random r = new Random();
	Color color;
	
	public PixelParticle(Vector2 position, Layer layer, int time, float size, Color color) {
		super(position, layer, time);
		setSize(size, size);
		this.color = color;
		
		if (time == 0) {
			Random r = new Random();
			while (this.time < 1) {
				this.time += r.nextInt(15)+10;
			}
		}
		spawntime = this.time;
		
		fade = true;
		objgravity = 0.5f*(8*size);
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void tick() {
		super.tick();
		if (time == spawntime-1) {
			movement.x = (r.nextFloat()-0.5f)*rec.width * 2;
			movement.y = (r.nextFloat()-0.5f)*rec.height * 3;
		}
	}
	
	Color c = new Color();
	Color cc = new Color();
	
	@Override
	public void preRender() {
		super.preRender();
		tmpimg.setColor(c.set(color).mul(cc.set(1f, 1f, 1f, time/spawntime)).mul(layer.tint));
	}
}
