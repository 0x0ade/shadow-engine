package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.entities.Player;

import java.util.Random;

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
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_ladder");
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.up.isDown) {
				p.movement.add(0, -p.movement.y - p.jumph * 0.35f);
			} else if (Input.down.isDown) {
				p.movement.add(0, -p.movement.y + p.jumph * 0.35f);
			} else {
				p.movement.add(0, -p.movement.y * 0.75f);
			}
			
			p.canJump = p.maxJump;
			
		}
	}
	
}
