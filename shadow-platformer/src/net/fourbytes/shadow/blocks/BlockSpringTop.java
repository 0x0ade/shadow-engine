package net.fourbytes.shadow.blocks;

import java.util.Random;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.TypeBlock;
import net.fourbytes.shadow.entities.Player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

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
	public Image getImage() {
		Image img = super.getImage();
		img.setColor(new Color(0f, 0f, 0f, 0f));
		return img;
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("white");
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.jump.isDown) {
				p.movement.add(0, -p.movement.y - p.JUMPH*1.414f);
				if (type_spring != null) {
					type_spring.doanim = true;
					type_spring.frame = 1;
					type_spring.imgupdate = true;
				}
			}
			
		}
	}
	
}
