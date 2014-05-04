package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.entities.particles.PlayerLightParticle;

import java.util.Random;

public class BlockPlayerLight extends BlockType {

	public int subframe = 0;
	public int frame = 0;

	public BlockPlayerLight() {
	}
	
	public static Random rand = new Random();

	@Override
	public void init() {
		light.set(0f, 0.5f, 0.7625f, 1f);
		solid = false;
		passSunlight = true;
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_point_white", 16, 16)[0][frame];
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Sounds.getSound("point").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			pixelify();
			PlayerLightParticle plp = new PlayerLightParticle(p, light);
			layer.add(plp);
			layer.remove(this);
		}
	}

	@Override
	public void preRender() {
		subframe += rand.nextInt(3);
		if (subframe > 12) {
			light.set(0f, 0.5f, 0.7625f, 1f);
			float ff = 5f;
			light.add((1f / ff) - ((float) Math.random()) / ff, (1f / ff) - ((float) Math.random()) / ff, (1f / ff) - ((float) Math.random()) / ff, 0f);
			frame++;
			subframe = 0;
			imgupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			pixdur = rand.nextInt(20)+20;
		}

		super.preRender();
	}

	@Override
	public void tint(int id, Image img) {
		super.tint(id, img);
		img.getColor().mul(light);
	}

}
