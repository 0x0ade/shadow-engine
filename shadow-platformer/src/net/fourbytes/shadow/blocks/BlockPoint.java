package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.entities.Player;

import java.util.Random;

public class BlockPoint extends BlockType {

	public int subframe = 0;
	public int frame = 0;
	
	public BlockPoint() {
	}
	
	public static Random rand = new Random();

	@Override
	public void init() {
		solid = false;
		passSunlight = true;
	}

	@Override 
	public void preRender() {
		subframe += rand.nextInt(3);
		if (subframe > 12) {
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
	public TextureRegion getTexture(int id) {
		return Images.split("block_point", 16, 16)[0][frame];
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Sounds.getSound("point").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			pixelify();
			layer.remove(this);
			p.points += 1;
		}
	}
	
}
