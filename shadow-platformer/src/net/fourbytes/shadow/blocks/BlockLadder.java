package net.fourbytes.shadow.blocks;

import java.util.Random;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.Player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlockLadder extends BlockType {
	
	public BlockLadder() {
	}
	
	public static Random rand = new Random();
	
	@Override 
	public void tick() {
		block.solid = false;
		block.passSunlight = true;
	}
	
	@Override
	public TextureRegion getTexture() {
		return new TextureRegion(Images.getTexture("block_ladder"));
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.up.isDown) {
				p.movement.add(0, -p.movement.y - p.JUMPH * 0.35f);
			} else if (Input.down.isDown) {
				p.movement.add(0, -p.movement.y + p.JUMPH * 0.35f);
			} else {
				p.movement.add(0, -p.movement.y * 0.75f);
			}
			
			p.canJump = p.maxJump;
			
		}
	}
	
}
