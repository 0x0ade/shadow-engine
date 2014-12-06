package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.IsSaveable;

public class Player extends Entity implements Input.KeyListener {
	
	@IsSaveable
	public int points = 0;
	@IsSaveable
	public float speed = 0.1f;
    @IsSaveable
    public float speedBoost = 6f;
	@IsSaveable
	public float jumph = 0.4f;
    @IsSaveable
    public boolean standing = true;
    @IsSaveable
	public float subframe = 0f;
    @IsSaveable
    public int frame = 0;
	public float hframe = 0f;
	@IsSaveable
	public int canJump = 0;
	@IsSaveable
	public int maxJump = 2;
	@IsSaveable
	public Vector2 spawnpos;
	
	public boolean canInteract = true;
	
	public Player(Vector2 position, Layer layer) {
		super(position, layer);
		Input.keylisteners.add(this);
		spawnpos = new Vector2(position);
		setSize(1f, 1f);
		//light.set(0.25f, 0.5f, 0.75f, 1f);
	}

	@Override
	public void dead() {
		health = MAXHEALTH;
		pos.set(spawnpos);
		movement.set(0f, 0f);
		hframe = 0f;
	}
	
	boolean invoid = false;
	
	@Override
	public void tick(float delta) {
		if (canInteract) {
			if (Input.left.isDown) {
				movement.add(-speed, 0f);
				facingLeft = true;
				standing = false;
				subframe += delta;
			}
			if (Input.right.isDown) {
				movement.add(speed, 0f);
				facingLeft = false;
				standing = false;
				subframe += delta;
			}
			if (!Input.right.isDown && !Input.left.isDown && canJump == maxJump) {
				standing = true;
				//imgupdate = true;
			}
		}
		if (canJump < maxJump) {
			standing = false;
			//imgupdate = true;
		}
		if (subframe >= (4f/60f)) {
			frame++;
			subframe = 0f;
            texupdate = true;
		}
		if (frame >= 4) {
			frame = 0;
			texupdate = true;
		}
		
		if (movement.y > 5f) {
			if (!invoid) {
				health = 0f;
				invoid = true;
			} else {
				invoid = false;
			}
		}
		
		super.tick(delta);

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
	public TextureRegion getTexture(int id) {
		return Images.split("player", 16, 16)[facingLeft?0:1][frame];
	}
	
	@Override
	public void keyDown(Input.Key key) {
		//canJump = 1; //comment line when not debugging
		if (key == Input.jump && canInteract) {
			if (canJump > 0) {
				Sounds.getSound("jump").play(1f, Sounds.calcPitch(1f, 0.3f), 0f);
				movement.y = -jumph;
				/*
				for (Particle pp : pixelify()) {
					pp.light.set(pp.color);
					pp.light.a = 0.0775f;
				}
				*/
			}
			canJump--;
		}
		
		if (key == Input.down) {
			for (Particle pp : pixelify()) {
                pp.pos.x += MathUtils.random(facingLeft ? -speedBoost : speedBoost) * MathUtils.random();
                pp.movement.x = MathUtils.random(facingLeft ? -0.5f : 0.5f);
                pp.objgravity = 0f;
 			}
            pos.add(facingLeft ? -speedBoost : speedBoost, 0f);
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
		
		if (hframe >= 1f) {
			return;
		}

		float alpha = (1f-hframe)*2f;
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
		float yy1 = pos.y - 0.05f - bgh;
		float yy2 = pos.y - 0.05f - bgh - 0.05f;
		
		Image white = bgwhite;
		if (white == null) {
			white = Images.getImage("white");
		}
		white.setScale(1f, -1f);
		
		white.setColor(0f, 0f, 0f, alpha);
		white.setPosition(xx1 + renderoffs.x, yy1+ renderoffs.y);
		white.setSize(bgw + renderoffs.width, bgh + renderoffs.height);
		white.draw(Shadow.spriteBatch, 1f);
		
		white = fgwhite;
		if (white == null) {
			white = Images.getImage("white");
		}
		white.setScale(1f, -1f);
		
		white.setColor(1f-1f*(health/MAXHEALTH), 1f*(health/MAXHEALTH), 0.2f, alpha);
		white.setPosition(xx2 + renderoffs.x, yy2 + renderoffs.y);
		white.setSize(fgw + renderoffs.width, fgh + renderoffs.height);
		white.draw(Shadow.spriteBatch, 1f);
		
	}
	
	@Override
	public void render() {
		super.render();
	}
	
}
