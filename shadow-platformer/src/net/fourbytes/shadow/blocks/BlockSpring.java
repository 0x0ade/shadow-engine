package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;

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
				block.imgupdate = true;
			}
			if (frame >= 4) {
				frame = 0;
				doanim = false;
				block.imgupdate = true;
			}
		}
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_spring", 16, 32);
		TextureRegion reg = null;
		reg = regs[0][frame];
		return reg;
	}
	
	@Override
	public void collide(Entity e) {
	}
	
}
