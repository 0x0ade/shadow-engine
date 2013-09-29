package net.fourbytes.shadow.blocks;

import java.util.Random;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.entities.Player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

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
			imgupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			block.pixdur = rand.nextInt(20)+20;
		}
	}
	
	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = TextureRegion.split(Images.getTexture("block_point"), 16, 16);
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
