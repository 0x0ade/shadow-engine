package net.fourbytes.shadow.blocks;

import java.util.Random;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.Player;
import net.fourbytes.shadow.TypeBlock;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BlockSpringTop extends BlockType {
	
	TypeBlock block_spring;
	BlockSpring type_spring;
	
	public BlockSpringTop() {
	}
	
	@Override 
	public void tick() {
		block.solid = false;
		block.passSunlight = true;
	}
	
	@Override
	public Image getImage() {
		Image img = super.getImage();
		img.setColor(new Color(0f, 0f, 0f, 0f));
		return img;
	}
	
	@Override
	public TextureRegion getTexture() {
		return new TextureRegion(Images.getTexture("white"));
	}
	
	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (Input.jump.isDown) {
				p.movement.add(0, -p.movement.y - p.JUMPH*1.414f);
				type_spring.doanim = true;
				type_spring.frame = 1;
				type_spring.imgupdate = true;
			}
			
		}
	}
	
}
