package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;

public class BlockSpring extends BlockType {
	
	public boolean doanim = false;
	public int subframe = 0;
	public int frame = 0;
	
	public BlockSpring() {
	}

	@Override
	public void init() {
		renderoffs.height = -1f;
		tickInView = true;
	}

	@Override 
	public void tick() {
		Array<Block> al = layer.get(Coord.get(pos.x, pos.y-1));
		boolean hasBlockTop = false;
		if (al != null) {
			for (Block b : al){
				if (b instanceof BlockSpringTop) {
					hasBlockTop = true;
				}
			}
		}

		if (!hasBlockTop) {
			BlockSpringTop topblock =
					(BlockSpringTop) BlockType.getInstance("BlockSpringTop", pos.x, pos.y - 1f, layer);
			topblock.block_spring = this;
			layer.add(topblock);
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
	public TextureRegion getTexture(int id) {
		return Images.split("block_spring", 16, 32)[0][frame];
	}
	
	@Override
	public void collide(Entity e) {
	}
	
}
