package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;

public class BlockSpring extends BlockType {
	
	public boolean doanim = false;
	public float subframe = 0;
	public int frame = 0;
	
	public BlockSpring() {
	}

	@Override
	public void init() {
		renderoffs.height = -1f;
		tickInView = true;
	}

	@Override 
	public void tick(float delta) {
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
			subframe += delta;
			if (subframe > 8f/60f) {
				frame++;
				subframe = 0;
				texupdate = true;
			}
			if (frame >= 4) {
				frame = 0;
				doanim = false;
				texupdate = true;
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
