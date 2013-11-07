package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;

public class BlockSpringTop extends BlockType {
	
	TypeBlock block_spring;
	BlockSpring type_spring;
	
	public BlockSpringTop() {
	}
	
	@Override 
	public void tick() {
		block.solid = false;
		block.passSunlight = true;
		
		if (block_spring == null || type_spring == null) {
			Array<Block> al = block.layer.get(Coord.get((int) block.pos.x, (int) block.pos.y + 1));
			for (Block b : al) {
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockSpring) {
					block_spring = (TypeBlock)b;
					type_spring = (BlockSpring) block_spring.type;
				}
			}
		}
	}
	
	@Override
	public Image getImage(int id) {
		Image img = super.getImage(id);
		img.setColor(new Color(0f, 0f, 0f, 0f));
		return img;
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
				if (type_spring != null) {
					type_spring.doanim = true;
					type_spring.frame = 1;
					type_spring.block.imgupdate = true;
				}
			}
			
		}
	}
	
}
