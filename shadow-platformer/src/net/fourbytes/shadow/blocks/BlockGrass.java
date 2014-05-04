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

	public BlockGrass() {
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.getTextureRegion("block_grass");
	}

	@Override
	public void init() {
		blending = false;
		if (hasGrassTop == -1) {
			hasGrassTop = Shadow.rand.nextInt(3);
		}
		if (hasGrassTop == 0) {
			Array<Block> blocks = layer.get(Coord.get(pos.x, pos.y-1f));
			if (blocks == null || blocks.size == 0) {
				Block grasstop = BlockType.getInstance("BlockGrassTop", pos.x, pos.y-1f, layer);
				grasstop.layer.add(grasstop);
			}
		}
	}
	
}
