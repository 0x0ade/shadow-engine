package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.entities.Player;

public class BlockLadder extends BlockType {

	public TextureRegion tex;

	public BlockLadder() {
	}
	
	@Override
	public void init() {
		dynamic = false;
		solid = false;
		passSunlight = true;
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return tex == null ? tex = Images.getTextureRegion("block_ladder") : tex;
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.up.isDown) {
				p.movement.y = -p.jumph * 0.35f;
			} else if (Input.down.isDown) {
				p.movement.y = p.jumph * 0.35f;
			} else {
				p.movement.y *= 0.75f;
			}
			
			p.canJump = p.maxJump;
			
		}
	}
	
}
