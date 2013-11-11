package net.fourbytes.shadow.entities.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.entities.Particle;
import net.fourbytes.shadow.entities.Player;

public class PlayerLightParticle extends Particle implements Input.KeyListener {

	public static boolean destroyed = false;

	public Color color;
	public Player player;

	public float speed = 0f;
	public float radius = 0f;

	public float shift = 0f;

	public PlayerLightParticle(Player player, Color color) {
		super(new Vector2(player.pos), player.layer, -1);
		this.interactive = true;
		this.player = player;
		this.color = new Color(color);

		float size = 0.0775f;
		setSize(size, size);

		radius = Shadow.rand.nextFloat()*0.5f+0.975f;
		speed = 2f + 1.475f - radius;
		shift = Shadow.rand.nextFloat()*360f;

		light.set(color);
		light.mul(0.4f);
		light.a = 0.2575f;

		solid = false;
		objgravity = 0f;

		Input.keylisteners.add(this);
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void tick() {
		if (time > spawntime) {
			spawntime = time;
		}

		if (time > -1) {
			light.set(color);
			light.mul(0.4f);
			light.a = 0.2575f;
			light.mul(time / spawntime);

			radius += speed/(((spawntime-time)+1f)*2f);

			if (time < 1) {
				layer.remove(this);
				return;
			}
		}

		super.tick();

		movement.set(0f, 0f);

		pos.set(player.pos);
		pos.add(player.rec.width/2f, player.rec.height/2f);
		pos.add(MathUtils.sinDeg(shift)*radius, MathUtils.cosDeg(shift)*radius);

		shift += speed;

		destroyed = false;
	}

	@Override
	public void preRender() {
		super.preRender();
	}

	@Override
	public void tint(int id, Image img) {
		super.tint(id, img);
		img.getColor().mul(color);
		if (time > -1) {
			img.getColor().mul(1f, 1f, 1f, time / spawntime);
		}
	}

	@Override
	public void keyDown(Input.Key key) {
		if (key == Input.jump && player.canJump == -1 && time <= -1 && !destroyed) {
			time = 8 + Shadow.rand.nextFloat()*6f;
			player.canJump = 1;
			player.keyDown(key);
			destroyed = true;
		}
	}

	@Override
	public void keyUp(Input.Key key) {
	}
}
