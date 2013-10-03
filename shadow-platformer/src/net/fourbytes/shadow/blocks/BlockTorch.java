package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;

import java.util.Random;

public class BlockTorch extends BlockType {

	int subframe = 0;
	int frame = 0;

	int wall = 0;

	public BlockTorch() {
	}

	public static Random rand = new Random();

	@Override
	public void tick() {
		subframe += rand.nextInt(5);
		block.solid = false;
		block.passSunlight = true;
		block.light.set(0.75f, 0.5f, 0.25f, 1f);
		if (subframe > 12) {
			frame++;
			subframe = 0;
			imgupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			block.pixdur = rand.nextInt(20) + 20;
		}

		wall = 0;
		block.renderoffs.width = 0f;
		block.renderoffs.x = 0f;
		Array<Block> al = block.layer.get(Coord.get(block.pos.x + 1f, block.pos.y));
		if (al != null && al.size != 0) {
			for (Block bb : al) {
				if (bb.solid) {
					wall = 1;
					block.renderoffs.width = -2f;
					block.renderoffs.x = 1f;
					break;
				}
			}
		}
		al = block.layer.get(Coord.get(block.pos.x - 1f, block.pos.y));
		if (al != null && al.size != 0) {
			for (Block bb : al) {
				if (bb.solid) {
					wall = -1;
					break;
				}
			}
		}

	}

	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = Images.split("block_torch", 16, 16);
		TextureRegion reg = null;
		reg = regs[frame][wall == 0 ? 0 : 1];
		return reg;
	}

	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Sounds.getSound("hurt").play(0.6f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			p.hurt(block, 0.05f);
			p.hit(block);
		}
	}

}
