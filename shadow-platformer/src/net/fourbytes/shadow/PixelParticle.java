package net.fourbytes.shadow;

import java.util.Random;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PixelParticle extends Particle {
	
	Random r = new Random();
	Texture tex;
	TextureRegion treg;
	Image img;
	Color color;
	
	public PixelParticle(Vector2 position, Layer layer, int time, float size, Color color) {
		super(position, layer, time);
		setSize(size, size);
		this.color = color;
		this.tex = Images.getTexture("white");
		
		if (time == 0) {
			Random r = new Random();
			while (this.time < 1) {
				this.time += r.nextInt(15)+10;
			}
		}
		spawntime = this.time;
		
		fade = true;
		objgravity = 0.5f*(8*size);
		
		updateTexture();
	}
	
	/*
	@Override
	public void tick() {
	}
	*/
	
	@Override
	public Image getImage() {
		return img;
	}
	
	@Override
	public TextureRegion getTexture() {
		return treg;
	}
	
	/**
	 * Call after updating color
	 */
	public void updateTexture() {
		if (tex == null) {
			treg = null;
			updateImage();
			return;
		}
		/*
		TextureData texdata = tex.getTextureData();
		texdata.prepare();
		Pixmap pixmap = texdata.consumePixmap();
		pixmap.setColor(color);
		pixmap.drawPixel(0, 0);
		pixmap.drawPixel(1, 1);
		pixmap.dispose();
		texdata.disposePixmap();*/
		treg = new TextureRegion(tex);
		updateImage();
	}
	
	/**
	 * Call after updating texture, runs automatically after {@link #updateTexture()}!
	 */
	public void updateImage() {
		if (tex == null) img = null;
		img = new Image(tex);
		img.setColor(color);
		cantint = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (time == spawntime-1) {
			movement.x = (r.nextFloat()-0.5f)*rec.width * 2;
			movement.y = (r.nextFloat()-0.5f)*rec.height * 3;
		}
	}
	
	Color c = new Color();
	Color cc = new Color();
	
	@Override
	public void preRender() {
		//tmpimg = new Image(tex);
		tmpimg = img;
		tmpimg.setColor(c.set(color).mul(cc.set(1f, 1f, 1f, time/spawntime)).mul(layer.tint));
		super.preRender();
	}
}
