package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Coord;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.map.Saveable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BlockGrass extends BlockType {
	
	@Saveable
	public int hasGrassTop = -1;
	@Saveable
	public boolean checkedGrassTop = false;
	
	public BlockGrass() {
		hasGrassTop = Shadow.rand.nextInt(3);
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("block_grass");
	}
	
	public void tick() {
		super.tick();
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
