package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.map.Saveable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.IntArray;

public class BlockGrassTop extends BlockType {
	
	public int[] order = {0, -2, -2};
	public int[] offsets = {0, 0, 0, 0, 0, 0};
	public float[] factors = {0, 0, 0, 0, 0, 0};
	public float[] speed1 = {0, 0, 0, 0, 0, 0};
	public float[] speed2 = {0, 0, 0, 0, 0, 0};
	public float[] height = {0, 0, 0, 0, 0, 0};
	Image[] imgcache;
	Sprite[] spritecache;
	
	public int frame = 0;
	
	public float depth = 0;
	
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
	public TextureRegion getTexture() {
		return getTexture(0);
	}
	
	@Override
	public Image getImage() {
		return getImage(0);
	}
	
	public TextureRegion getTexture(int i) {
		TextureRegion[][] regs = Images.split("block_grasstop", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][i];
		if (imgcache == null || spritecache == null) {
			imgcache = new Image[regs[0].length];
			spritecache = new Sprite[regs[0].length];
		}
		return reg;
	}
	
	public Image getImage(int i) {
		if (imgcache == null || imgcache[i] == null) {
			Image img = new Image(getTexture(i));
			imgcache[i] = img;
		}
		return imgcache[i];
	}
	
	public Sprite getSprite(int i) {
		if (spritecache == null || spritecache[i] == null) {
			Sprite sprite = new Sprite(getTexture(i));
			spritecache[i] = sprite;
		}
		return spritecache[i];
	}
	
	public void tick() {
		block.solid = false;
		block.rendertop = 0x02;
		
		frame++;
		super.tick();
	}
	
	@Override
	public void preRender() {
		block.tmpimg = getImage(0);
		for (int i = 0; i < spritecache.length; i++) {
			Image img = getImage(i);
			
			img.setPosition(block.pos.x + block.renderoffs.x, block.pos.y + block.rec.height + block.renderoffs.y + depth);
			img.setSize(block.rec.width + block.renderoffs.width, (block.rec.height + block.renderoffs.height) * height[i]);
			img.setScaleY(-1f);
			
			Sprite sprite = getSprite(i);
			
			sprite.setPosition(block.pos.x + block.renderoffs.x, block.pos.y + block.rec.height + block.renderoffs.y + depth);
			sprite.setSize(block.rec.width + block.renderoffs.width, -(block.rec.height + block.renderoffs.height) * height[i]);
			
			int offsetframe = frame + offsets[i];
			float offs = (float)Math.sin(offsetframe/(1.3f*speed1[i]))/8f + ((float)Math.cos(offsetframe/(1.2f*speed2[i]))/8f);
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
			
			sprite.draw(Shadow.spriteBatch);
		}
	}
	
	@Override
	public void renderTop() {
		for (int i = order.length-2; i < order.length; i++) {
			Sprite sprite = spritecache[order[i]];
			
			sprite.draw(Shadow.spriteBatch);
		}
	}
	
}
