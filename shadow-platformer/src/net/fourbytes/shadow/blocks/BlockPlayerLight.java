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

	int subframe = 0;
	int frame = 0;

	public BlockPlayerLight() {
	}
	
	public static Random rand = new Random();
	
	@Override 
	public void tick() {
		subframe += rand.nextInt(3);
		if (block.solid) {
			block.light.set(0f, 0.5f, 0.7625f, 1f);
		}
		block.solid = false;
		block.passSunlight = true;
		if (subframe > 12) {
			block.light.set(0f, 0.5f, 0.7625f, 1f);
			float ff = 5f;
			block.light.add((1f / ff) - ((float) Math.random()) / ff, (1f / ff) - ((float) Math.random()) / ff, (1f / ff) - ((float) Math.random()) / ff, 0f);
			frame++;
			subframe = 0;
			block.imgupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			block.pixdur = rand.nextInt(20)+20;
		}
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_point_white", 16, 16);
		TextureRegion reg = regs[0][frame];
		return reg;
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Sounds.getSound("point").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			block.pixelify();
			PlayerLightParticle plp = new PlayerLightParticle(p, block.light);
			block.layer.add(plp);
			block.layer.remove(block);
		}
	}

	@Override
	public void preRender() {
		super.preRender();
		for (int i = 0; i < block.imgIDs.length; i++) {
			int id = block.imgIDs[i];
			Image img = getImage(id);
			img.getColor().mul(block.light);
		}
	}

}
