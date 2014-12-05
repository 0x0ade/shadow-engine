package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Particle;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;
import net.fourbytes.shadow.systems.IParticleManager;
import net.fourbytes.shadow.utils.Garbage;

@IsSaveable(false)
public class BlockGrassTop extends BlockType {
	
	public int[] order = {0, -2, -2};
	public float[] offsets = {0, 0, 0, 0, 0, 0};
	public float[] factors = {0, 0, 0, 0, 0, 0};
	public float[] speed1 = {0, 0, 0, 0, 0, 0};
	public float[] speed2 = {0, 0, 0, 0, 0, 0};
	public float[] height = {0, 0, 0, 0, 0, 0};
	public Sprite[] spritecache;

	public float frame = 0;
	
	public float depth = 0;

	public float collision = 0f;
	
	public BlockGrassTop() {
		frame = Shadow.rand.nextInt(1000);
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = MathUtils.random(6f);
			factors[i] = 1f - ((float)Shadow.rand.nextInt(100))/300f;
			speed1[i] = 10f+Shadow.rand.nextInt(10);
			speed2[i] = 20f+Shadow.rand.nextInt(20);
			height[i] = 1f + ((float)Shadow.rand.nextInt(100))/450f;
			
			if (i < order.length && i > 0) {
				int orderi = -1;
				while (orderi == -1) {
					orderi = Shadow.rand.nextInt(offsets.length);
					for (int x : order) {
						if (orderi == x) {
							orderi = -1;
							break;
						}
					}
				}
				order[i] = orderi;
			}
		}
		depth = 0.075f + ((float)Shadow.rand.nextInt(100))/3000f;
	}

	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_grasstop", 16, 16);
		if (images == null || images.length != regs[0].length || spritecache == null) {
			images = new Image[regs[0].length];
			spritecache = new Sprite[regs[0].length];
		}
		return regs[0][id];
	}

	@Override
	public Image getImage(int id) {
		if (images == null || images[id] == null) {
			Image img = new Image(getTexture(id));
			images[id] = img;
		}
		return images[id];
	}

	public Sprite getSprite(int i) {
		if (spritecache == null || spritecache[i] == null) {
			Sprite sprite = new Sprite(getTexture(i));
			spritecache[i] = sprite;
		}
		return spritecache[i];
	}

	@Override
	public void init() {
		tickInView = true;
		solid = false;
		rendertop = 0x02;
		imgIDs = order;
	}

	@Override
	public void tick(float delta) {
		//frame++;
		super.tick(delta);
	}

	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			if (collision <= 0f) {
				Vector2 pos = Garbage.vec2s.getNext();
				for (int i = 0; i < 3 + Shadow.rand.nextInt(3); i++) {
					pos.set(this.pos);
					pos.x += rec.width/2f;
					pos.y += rec.height/2f;
					pos.x -= MathUtils.random(-0.5f, 0.5f);
					pos.y -= MathUtils.random(-0.25f, 0.25f);
					Particle go = layer.level.systems.get(IParticleManager.class).create("GrassParticle", pos, layer, null, 0, 0);
					layer.add(go);
				}
			}
			collision = 0.25f;
		}
	}

	@Override
	public void frame(float delta) {
		frame += delta;
        collision -= delta;
	}

	@Override
	public void preRender() {
		if (spritecache == null) {
			getTexture(0);
		}
		for (int i = 0; i < spritecache.length; i++) {
			Image img = getImage(i);

			img.setPosition(pos.x + renderoffs.x, pos.y + rec.height + renderoffs.y + depth);
			img.setSize(rec.width + renderoffs.width, (rec.height + renderoffs.height) * height[i]);
			img.setScaleY(-1f);

			Sprite sprite = getSprite(i);

			sprite.setColor(img.getColor());
			sprite.setPosition(pos.x + renderoffs.x, pos.y + rec.height + renderoffs.y + depth);
			sprite.setSize(rec.width + renderoffs.width, -(rec.height + renderoffs.height) * height[i]);
			
			float offsetframe = (frame + offsets[i])*60f;
			float offs = MathUtils.sin(offsetframe / (1.3f * speed1[i]))/8f + (MathUtils.cos(offsetframe/(1.2f*speed2[i]))/8f);
			offs *= 1.15f;
			offs *= factors[i];
			float[] verts = sprite.getVertices();
			float x2 = sprite.getX();
			float x3 = x2 + sprite.getWidth();
			x2 += offs;
			x3 += offs;
			verts[SpriteBatch.X2] = x2;
			verts[SpriteBatch.X3] = x3;
		}

		imgupdate = false;
		texupdate = false;
	}
	
	@Override
	public void render() {
		for (int i = 0; i < order.length-2; i++) {
			Sprite sprite = spritecache[order[i]];
			sprite.setColor(images[order[i]].getColor());

			sprite.draw(Shadow.spriteBatch);
		}
	}
	
	@Override
	public void renderTop() {
		for (int i = order.length-2; i < order.length; i++) {
			Sprite sprite = spritecache[order[i]];
			sprite.setColor(images[order[i]].getColor());

			sprite.draw(Shadow.spriteBatch);
		}
	}

}
