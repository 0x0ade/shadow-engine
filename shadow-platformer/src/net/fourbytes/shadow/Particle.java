package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.map.IsSaveable;

public final class Particle extends Entity {

    @IsSaveable
	public float time = 0;
    @IsSaveable
	public float spawntime = 0;

	public ParticleType type;

	public Particle() {
		super(null, null);
	}

	@Override
	public TextureRegion getTexture(int id) {
		return type.getTexture(this, id);
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		if (spawntime <= 0f) {
			spawntime = time;
		}

		if (time <= 0f) {
			layer.remove(this);
			return;
		}

		if (time > 0f) {
			time -= delta;
		}

		imgupdate = true;

		type.tick(this);
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
		if (oa < 0f) {
			oa = c.a;
		}
		c.a = oa*af;
	}

	public void create(Vector2 pos, Layer layer, ParticleType type) {
		if (this.pos == null) {
			this.pos = new Vector2(pos);
		} else {
			this.pos.set(pos);
		}
		this.layer = layer;
		this.type = type;

		this.imgIDs = type.getTextureIds(this);
	}

	public void reset() {
		layer = null;
		type = null;

		oa = -1f;
		alpha = 1f;
		objgravity = 0f;
        slowdown = 0f;
		solid = true;
        pos.set(0f, 0f);
		movement.set(0f, 0f);
		renderoffs.set(0f, 0f, 0f, 0f);

		light.set(1f, 1f, 1f, 0f);
		tintSunlight.set(1f, 1f, 1f, 1f);
		tintDarklight.set(0f, 0f, 0f, 1f);
		passSunlight = false;

		imgIDs = new int[] {0};

		time = 0;
		spawntime = 0;
	}

}
