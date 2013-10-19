package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.map.Saveable;

public class BlockGrass extends BlockType {
	
	@Saveable
	public int hasGrassTop = -1;
	@Saveable
	public boolean checkedGrassTop = false;
	
	public BlockGrass() {
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_grass");
	}
	
	public void tick() {
		block.blending = false;
		if (hasGrassTop == -1) {
			hasGrassTop = Shadow.rand.nextInt(3);
		}
		if (!checkedGrassTop && hasGrassTop == 0) {
			checkedGrassTop = true;
			
			Array<Block> blocks = block.layer.get(Coord.get(block.pos.x, block.pos.y-1f));
			if (blocks == null || blocks.size == 0) {
				Block grasstop = BlockType.getInstance("BlockGrassTop", block.pos.x, block.pos.y-1f, block.layer);
				grasstop.layer.add(grasstop);
			}
		}
	}
	
}
