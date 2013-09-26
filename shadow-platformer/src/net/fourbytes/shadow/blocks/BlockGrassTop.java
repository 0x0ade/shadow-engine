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
	public int[] offsets = {0, 0, 0};
	public float[] factors = {1f, 1.125f, 1.4f};
	public float[] speed1 = {0, 0, 0};
	public float[] speed2 = {0, 0, 0};
	public float[] height = {0, 0, 0};
	Image[] imgcache = {null, null, null};
	Sprite[] spritecache = {null, null, null};
	
	public int frame = 0;
	
	public float depth = 0;
	
	public BlockGrassTop() {
		frame = Shadow.rand.nextInt(1000);
		for (int i = 0; i < offsets.length; i++) {
			offsets[i] = Shadow.rand.nextInt(400);
			speed1[i] = 10f+Shadow.rand.nextInt(20);
			speed2[i] = 20f+Shadow.rand.nextInt(10);
			height[i] = 1f + ((float)Shadow.rand.nextInt(100))/450f;
			if (i > 0) {
				int orderi = -1;
				while (orderi == -1) {
					orderi = Shadow.rand.nextInt(order.length);
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
		TextureRegion[][] regs = TextureRegion.split(Images.getTexture("block_grasstop"), 16, 16);
		TextureRegion reg = null;
		reg = regs[0][i];
		return reg;
	}
	
	public Image getImage(int i) {
		if (imgcache[i] == null) {
			Image img = new Image(getTexture(i));
			imgcache[i] = img;
		}
		return imgcache[i];
	}
	
	public Sprite getSprite(int i) {
		if (spritecache[i] == null) {
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
		//TODO
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
			float offs = (float)Math.sin(offsetframe/speed1[i])/8f + ((float)Math.cos(offsetframe/speed2[i])/8f);
			offs *= 1.2f;
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
		for (int i = 0; i < spritecache.length-2; i++) {
			Sprite sprite = spritecache[order[i]];
			
			sprite.draw(Shadow.spriteBatch);
		}
	}
	
	@Override
	public void renderTop() {
		for (int i = spritecache.length-2; i < spritecache.length; i++) {
			Sprite sprite = spritecache[order[i]];
			
			sprite.draw(Shadow.spriteBatch);
		}
	}
	
}
