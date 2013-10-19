package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.entities.particles.PixelParticle;
import net.fourbytes.shadow.map.Saveable;

public class BlockTorch extends BlockType implements BlockLogic {

	@Saveable
	boolean triggered = true;
	boolean tmptriggered = true;

	int subframe = 0;
	int frame = 0;

	int wall = 0;

	public BlockTorch() {
	}

	public BlockTorch(int triggered) {
		this.triggered = triggered == 1;
		this.tmptriggered = this.triggered;
	}

	@Override
	public void tick() {
		subframe += Shadow.rand.nextInt(5);
		if (block.solid) {
			block.light.set(0.75f, 0.5f, 0.25f, 1f);
		}
		block.solid = false;
		block.passSunlight = true;
		if (triggered) {
			if (subframe > 12) {
				frame++;
				subframe = 0;
				block.imgupdate = true;
				block.light.set(0.75f, 0.5f, 0.25f, 1f);
				block.light.mul(1f-Shadow.rand.nextFloat()*0.2f);
				block.light.a = 1f-Shadow.rand.nextFloat()*0.15f;

				for (int i = 0; i < Shadow.rand.nextInt(6)-4; i++) {
					Vector2 pos = new Vector2(block.pos);
					pos.add(block.rec.width/2f, block.rec.height/2f);
					pos.add(Shadow.rand.nextFloat()-0.5f, Shadow.rand.nextFloat()-0.5f);

					Color color = new Color(block.light);
					color.mul(1f-(Shadow.rand.nextFloat()/10f));

					PixelParticle pp = new PixelParticle(pos, block.layer, 0, 0.0775f, color);
					pp.objgravity *= 0.5f;
					pp.layer.add(pp);
				}
			}
			if (frame >= 4) {
				frame = 0;
			}
		} else {
			block.light.set(0f, 0f, 0f, 0f);
		}
		if (triggered != tmptriggered) {
			block.imgupdate = true;
		}

		wall = 0;
		block.renderoffs.width = 0f;
		block.renderoffs.x = 0f;
		Array<Block> al = block.layer.get(Coord.get(block.pos.x+1f, block.pos.y));
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
		al = block.layer.get(Coord.get(block.pos.x-1f, block.pos.y));
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
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_torch_"+(triggered?"on":"off"), 16, 16);
		TextureRegion reg = null;
		reg = regs[frame][wall==0?0:1];
		return reg;
	}

	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (triggered && e instanceof Player) {
			Sounds.getSound("hurt").play(0.6f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			p.hurt(block, 0.05f);
			p.hit(block);
		}
	}

	@Override
	public boolean triggered() {
		return triggered;
	}

	@Override
	public void handle(boolean triggered) {
		this.triggered = triggered;
	}

	@Override
	public LogicType getType() {
		return LogicType.INPUT;
	}
}
