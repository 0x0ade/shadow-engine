package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.entities.particles.GrassParticle;

public class BlockGrassTop extends BlockType {
	
	public int[] order = {0, -2, -2};
	public int[] offsets = {0, 0, 0, 0, 0, 0};
	public float[] factors = {0, 0, 0, 0, 0, 0};
	public float[] speed1 = {0, 0, 0, 0, 0, 0};
	public float[] speed2 = {0, 0, 0, 0, 0, 0};
	public float[] height = {0, 0, 0, 0, 0, 0};
	public Sprite[] spritecache;

	public int frame = 0;
	
	public float depth = 0;

	public int collision = 0;
	
	public BlockGrassTop() {
		frame = Shadow.rand.nextInt(1000);
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = Shadow.rand.nextInt(400);
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
	public void tick() {
		//frame++;
		collision--;
		super.tick();
	}

	@Override
	public void collide(Entity e) {
		if (e instanceof Player) {
			if (collision <= 0) {
				for (int i = 0; i < 3 + Shadow.rand.nextInt(3); i++) {
					Vector2 pos = new Vector2(this.pos);
					pos.x += rec.width/2f;
					pos.y += rec.height/2f;
					pos.x -= Shadow.rand.nextFloat() - 0.5f;
					pos.y -= 0.5f*Shadow.rand.nextFloat() - 0.25f;
					GrassParticle go = new GrassParticle(pos, layer);
					layer.add(go);
				}
			}
			collision = 32;
		}
	}
	
	@Override
	public void preRender() {
		frame++;
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
			
			int offsetframe = frame + offsets[i];
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
