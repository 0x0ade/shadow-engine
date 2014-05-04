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
	public boolean triggered = true;
	public boolean tmptriggered = true;

	public int subframe = 0;
	public int frame = 0;

	public int wall = 0;

	public BlockTorch() {
	}

	public BlockTorch(int triggered) {
		this.triggered = triggered == 1;
		this.tmptriggered = this.triggered;
	}

	@Override
	public void init() {
		light.set(0.75f, 0.5f, 0.25f, 1f);
		solid = false;
		passSunlight = true;
		tickInView = true;
	}

	@Override
	public void tick() {
		if (triggered != tmptriggered) {
			imgupdate = true;
		}

		wall = 0;
		renderoffs.width = 0f;
		renderoffs.x = 0f;
		Array<Block> al = layer.get(Coord.get(pos.x+1f, pos.y));
		if (al != null && al.size != 0) {
			for (Block bb : al) {
				if (bb.solid) {
					wall = 1;
					renderoffs.width = -2f;
					renderoffs.x = 1f;
					break;
				}
			}
		}
		al = layer.get(Coord.get(pos.x-1f, pos.y));
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
	public void preRender() {
		subframe += Shadow.rand.nextInt(5);
		if (triggered) {
			if (subframe > 12) {
				frame++;
				subframe = 0;
				imgupdate = true;
				light.set(0.75f, 0.5f, 0.25f, 1f);
				light.mul(1f-Shadow.rand.nextFloat()*0.2f);
				light.a = 1f-Shadow.rand.nextFloat()*0.15f;

				for (int i = 0; i < Shadow.rand.nextInt(6)-4; i++) {
					Vector2 pos = new Vector2(this.pos);
					pos.add(rec.width/2f, rec.height/2f);
					pos.add(Shadow.rand.nextFloat()-0.5f, Shadow.rand.nextFloat()-0.5f);

					Color color = new Color(light);
					color.mul(1f-(Shadow.rand.nextFloat()/10f));

					PixelParticle pp = new PixelParticle(pos, layer, 0, 0.0775f, color);
					pp.objgravity *= 0.5f;
					pp.layer.add(pp);
				}
			}
			if (frame >= 4) {
				frame = 0;
			}
		} else {
			light.set(0f, 0f, 0f, 0f);
		}
		super.preRender();
	}

	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_torch_"+(triggered?"on":"off"), 16, 16)[frame][wall==0?0:1];
	}

	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (triggered && e instanceof Player) {
			Sounds.getSound("hurt").play(0.6f, Sounds.calcPitch(1f, 0.2f), 0f);
			Player p = (Player) e;
			p.hurt(this, 0.05f);
			p.hit(this);
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
