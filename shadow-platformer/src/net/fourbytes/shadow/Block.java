package net.fourbytes.shadow;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Block extends GameObject {
	
	public String subtype = "";
	public boolean interactive = false;
	public boolean rendertop = false;
	public Rectangle colloffs = new Rectangle(0, 0, 0, 0);
	
	public Block(Vector2 pos, Layer layer) {
		super(pos, layer);
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("block_test");
	}

	@Override
	public void tick() {
		super.tick();
	}
	
	public void collide(Entity e) {
	}
}
