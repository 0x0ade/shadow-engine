package net.fourbytes.shadow.blocks;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Garbage;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.Player;
import net.fourbytes.shadow.TypeBlock;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BlockSpring extends BlockType {
	
	boolean doanim = false;
	int subframe = 0;
	int frame = 0;
	
	public BlockSpring() {
	}
	
	boolean first = true;
	
	@Override 
	public void tick() {
		block.renderoffs.height = -1f;
		
		Array<Block> al = block.layer.get(Coord.get(block.pos.x, block.pos.y-1));
		first = true;
		if (al != null) {
			for (Block b : al){
				if (b instanceof TypeBlock && ((TypeBlock)b).type instanceof BlockSpringTop) {
					first = false;
				}
			}
		}
		if (first) {
			first = false;
			
			BlockSpringTop instance = new BlockSpringTop();
			instance.subtype = "BlockSpringTop";
			instance.block_spring = block;
			instance.type_spring = this;
			Block topblock = new TypeBlock(new Vector2(block.pos.x, block.pos.y-1f), block.layer, instance);
			topblock.subtype = instance.subtype;
			block.layer.add(topblock);
		}
		if (doanim) {
			subframe++;
			if (subframe > 8) {
				frame++;
				subframe = 0;
				imgupdate = true;
			}
			if (frame >= 4) {
				frame = 0;
				doanim = false;
				imgupdate = true;
			}
		}
	}
	
	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = TextureRegion.split(Images.getTexture("block_spring"), 16, 32);
		TextureRegion reg = null;
		reg = regs[0][frame];
		return reg;
	}
	
	@Override
	public void collide(Entity e) {
	}
	
}
