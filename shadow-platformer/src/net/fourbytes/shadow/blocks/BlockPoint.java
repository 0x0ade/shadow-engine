package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.entities.Player;

import java.util.Random;

public class BlockPoint extends BlockType {
	
	int subframe = 0;
	int frame = 0;
	
	public BlockPoint() {
	}
	
	public static Random rand = new Random();
	
	@Override 
	public void tick() {
		subframe += rand.nextInt(3);
		block.solid = false;
		block.passSunlight = true;
		if (subframe > 12) {
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
		TextureRegion[][] regs = Images.split("block_point", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][frame];
		return reg;
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Sounds.getSound("point").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			block.pixelify();
			block.layer.remove(block);
			p.POINTS += 1;
		}
	}
	
}
