package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;

public class BlockSpringTop extends BlockType {

	public BlockSpring block_spring;

	public BlockSpringTop() {
	}

	@Override
	public void init() {
		solid = false;
		passSunlight = true;
		alpha = 0f;
		tickInView = true;
	}

	@Override 
	public void tick() {
		if (block_spring == null) {
			Array<Block> al = layer.get(Coord.get(pos.x, pos.y+1));
			for (Block b : al) {
				if (b instanceof BlockSpring) {
					block_spring = (BlockSpring)b;
				}
			}
		}
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.jump.isDown) {
				p.movement.add(0, -p.movement.y - p.jumph*1.414f);
				if (block_spring != null) {
					block_spring.doanim = true;
					block_spring.frame = 1;
					block_spring.imgupdate = true;
				}
			}
			
		}
	}

	@Override
	public void preRender() {
	}

	@Override
	public void render() {
	}
	
}
