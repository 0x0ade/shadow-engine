package net.fourbytes.shadow.entities;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Vector;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.Fonts;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.Images;
import net.fourbytes.shadow.Input;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.KeyListener;
import net.fourbytes.shadow.map.Saveable;
import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Player extends Entity implements Input.KeyListener {
	
	@Saveable
	public int POINTS = 0;
	
	public static final float SPEED = 0.1f;
	public static final float JUMPH = 0.4f;
	public boolean standing = true;
	int subframe = 0;
	public int frame = 0;
	int hframe = 0;
	@Saveable
	public int canJump = 0;
	@Saveable
	public int maxJump = 2;
	@Saveable
	public Vector2 spawnpos;
	
	public boolean canInteract = true;
	
	public Player(Vector2 position, Layer layer) {
		super(position, layer);
		Input.keylisteners.add(this);
		spawnpos = new Vector2(position);
		setSize(1f, 1f);
	}
	
	@Override
	public void dead() {
		health = MAXHEALTH;
		pos.set(spawnpos);
		movement.set(0, 0);
		hframe = 0;
	}
	
	boolean invoid = false;
	
	@Override
	public void tick() {
		if (canInteract) {
			if (Input.left.isDown) {
				movement.add(-SPEED, 0f);
				facingLeft = true;
				standing = false;
				imgupdate = true;
				subframe++;
			}
			if (Input.right.isDown) {
				movement.add(SPEED, 0f);
				facingLeft = false;
				standing = false;
				imgupdate = true;
				subframe++;
			}
			if (!Input.right.isDown && !Input.left.isDown && canJump == maxJump) {
				standing = true;
				imgupdate = true;
			}
		}
		if (canJump < maxJump) {
			standing = false;
			imgupdate = true;
		}
		if (subframe >= 4) {
			frame++;
			subframe = 0;
		}
		if (frame >= 4) {
			frame = 0;
			imgupdate = true;
		}
		
		if ((layer != null && layer.level != null && layer.level.hasvoid) || movement.y > 5f) {
			if (pos.y > layer.level.tiledh && !invoid) {
				health = 0f;
				invoid = true;
			} else {
				invoid = false;
			}
		}
		
		super.tick();
		
		if (this != layer.level.player) {
			hframe = 0;
		} else {
			hframe++;
		}
		
		/*
		if (facingLeft) {
			renderoffs.width = -rec.width*2;
			renderoffs.x = rec.width;
		} else {
			renderoffs.width = 0;
			renderoffs.x = 0;
		}
		*/
		
	}
	
	@Override
	public TextureRegion getTexture() {
		TextureRegion[][] regs = Images.split("player", 16, 16);
		TextureRegion reg = null;
		reg = regs[facingLeft?0:1][frame];
		return reg;
	}
	
	@Override
	public void keyDown(Input.Key key) {
		//canJump = 1; //comment line when not debugging
		if (key == Input.jump && canJump > 0 && canInteract) {
			Sounds.getSound("jump").play(1f, Sounds.calcPitch(1f, 0.3f), 0f);
			movement.add(0f, -movement.y - JUMPH);
			canJump--;
			//pixelify();
		}
		
		if (key == Input.down) {
			//health -= 0.1f;
			//hurt(null, 0.1f);
		}
	}

	@Override
	public void keyUp(Input.Key key) {
	}
	
	@Override
	public void hurt(GameObject go, float damage) {
		super.hurt(go, damage);
		hframe = 0;
	}
	
	//Reducing garbage
	Image bgwhite;
	Image fgwhite;
	
	@Override
	public void renderHealth() {
		if (this == layer.level.player) {
			return;
		}
		
		if (hframe >= 40) {
			return;
		}
		
		float alpha = (40-hframe)/15f;
		if (alpha > 1f) {
			alpha = 1f;
		}
		
		float bgw = rec.width*1.1f*MAXHEALTH+0.1f;
		float fgw = rec.width*1.1f*health;
		float bgh = 0.15f;
		float fgh = 0.05f;
		
		bgw *= Shadow.cam.cam.zoom;
		fgw *= Shadow.cam.cam.zoom;
		bgh *= Shadow.cam.cam.zoom;
		fgh *= Shadow.cam.cam.zoom;
		
		float xx1 = pos.x + rec.height/2 - bgw/2;
		float xx2 = pos.x + rec.height/2 - bgw/2 + 0.05f;
		float yy1 = pos.y - 0.1f - bgh;
		float yy2 = pos.y - 0.1f - bgh - 0.05f;
		
		Image white = bgwhite;
		if (white == null) {
			white = Images.getImage("white", true);
		}
		white.setScaleY(-1f);
		
		white.setColor(0f, 0f, 0f, alpha);
		white.setPosition(xx1 + renderoffs.x, yy1+ renderoffs.y);
		white.setSize(bgw + renderoffs.width, bgh + renderoffs.height);
		white.draw(Shadow.spriteBatch, 1f);
		
		white = fgwhite;
		if (white == null) {
			white = Images.getImage("white", true);
		}
		white.setScaleY(-1f);
		
		white.setColor(1f-1f*(health/MAXHEALTH), 1f*(health/MAXHEALTH), 0.2f, alpha);
		white.setPosition(xx2 + renderoffs.x, yy2 + renderoffs.y);
		white.setSize(fgw + renderoffs.width, fgh + renderoffs.height);
		white.draw(Shadow.spriteBatch, 1f);
		
	}
	
	BitmapFont font = Fonts.light_normal;

	@Override
	public void render() {
		super.render();
	}
	
}
