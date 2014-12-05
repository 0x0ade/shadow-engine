package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.utils.Garbage;

public class BlockTorch extends BlockType implements BlockLogic {

	@IsSaveable
	public boolean triggered = true;
	public boolean tmptriggered = true;

	public float subframe = 0;
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
	public void tick(float delta) {
		if (triggered != tmptriggered) {
			texupdate = true;
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
	public void frame(float delta) {
		subframe += delta * Shadow.rand.nextInt(5);
		if (triggered) {
			if (subframe > 12f/60f) {
				frame++;
				subframe = 0;
				texupdate = true;
				light.set(0.75f, 0.5f, 0.25f, 1f);
				light.mul(1f-Shadow.rand.nextFloat()*0.2f);
				light.a = 1f-Shadow.rand.nextFloat()*0.15f;

				Vector2 pos = Garbage.vec2s.getNext();
				for (int i = 0; i < Shadow.rand.nextInt(6)-4; i++) {
					pos.set(this.pos);
					pos.add(rec.width/2f, rec.height/2f);
					pos.add(Shadow.rand.nextFloat()-0.5f, Shadow.rand.nextFloat()-0.5f);

					Color color = Garbage.colors.getNext().set(light);
					color.mul(1f-(Shadow.rand.nextFloat()/10f));

					Particle pp = layer.level.systems.get(IParticleManager.class).create("PixelParticle", pos, layer, color, 0.0775f, 0);
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
	}

	@Override
	public void preRender() {
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
