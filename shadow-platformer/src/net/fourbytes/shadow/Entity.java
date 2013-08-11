package net.fourbytes.shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public abstract class Entity extends GameObject {
	
	public Vector2 movement = new Vector2(0, 0);
	public boolean facingLeft = true;
	public float slowdown = 0.8f;
	public Vector2 oldpos;
	public float objgravity = 1f;
	
	public float MAXHEALTH = 1f;
	public float health = 1f;
	
	public Entity(Vector2 position, Layer layer) {
		super(position, layer);
		inithealth(1f);
		
		pixfac = 2;
	}
	
	public void inithealth(float h) {
		MAXHEALTH = h;
		health = h;
	}
	
	public void dead() {
	}
	
	public void tick() {
		super.tick();
		
		if (health > MAXHEALTH) {
			health = MAXHEALTH;
		}
		
		if (health <= 0) {
			dead();
			return;
		}
		
		if (layer == null) {
			//Just skip the tick...
			return;
		}
		
		movement.x += layer.level.xgravity*objgravity;
		movement.y += layer.level.gravity*objgravity;
		
		if (oldpos == null) {
			oldpos = new Vector2();
		}
		oldpos.set(pos);
		
		pos.y += movement.y;
		pos.x += movement.x;
		
		if (solid) {
			//Vector<Block> blocks = (Vector<Block>)layer.blocks.clone();
			for (float x = pos.x-rec.width*2; x <= pos.x+rec.width*3; x++) {
				for (float y = pos.y-rec.height*2; y <= pos.y+rec.height*3; y++) {
					Array<Block> blocks = layer.get(Coord.get(x, y));
					if (blocks != null) {
						for (Block b : blocks) {
							if (b == null) continue;
							if (!b.solid) collide(b, true);
							else collide(b, false);
						}
					}
				}
			}
			
			for (Entity e : layer.entities) {
				if (e == null) continue;
				if (e == this) continue;
				if (!e.solid) collide(e, true);
				else collide(e, true); //TODO: Examine some cool stuff about that one.
			}
		}
		
		movement.x *= 1f-slowdown;
	}
	
	Rectangle tmper = new Rectangle();
	Rectangle or = new Rectangle();
	
	Rectangle calcCollide() {
		float rad = (rec.width + rec.height)/2;
		rad *= 0.05f;
		tmper.set(pos.x + rad, pos.y + rad, rec.width - rad*2, rec.height - rad*2);
		return tmper;
	}
	
	public void collide(GameObject o, boolean canpass) {
		Rectangle er = calcCollide();
		or.set(o.pos.x, o.pos.y, o.rec.width, o.rec.height);
		if (o instanceof Block) {
			Block b = (Block) o;
			or.x += b.colloffs.x;
			or.y += b.colloffs.y;
			or.width += b.colloffs.width;
			or.height += b.colloffs.height;
		}
		
		float tmpy = 0f;
		
		boolean collide = false;
		
		if (or.overlaps(er)) {
			if (!canpass) {
				if (movement.y > 0) {
					if (this instanceof Player) {
						((Player)this).canJump = ((Player)this).maxJump;
					}
					if (this instanceof Mob) {
						((Mob)this).canJump = ((Mob)this).maxJump;
					}
					pos.y = o.pos.y-rec.height;
				}
				pos.y -= movement.y;
				pos.y = oldpos.y;
				tmpy = movement.y;
				movement.y = 0f;
				
				er = calcCollide();
			}
			collide = true;
		}
		
		if (or.overlaps(er)) {
			if (!canpass) {
				//pos.x -= movement.x;
				pos.x = oldpos.x;
				movement.x = 0f;
				pos.y += tmpy;
				
				er = calcCollide();
			}
			collide = true;
		}
		
		//if (collide && o instanceof Block) {
		//	Block b = (Block) o;
		//	b.collide(this);
		//}
		if (collide) {
			//o.highlight();
			o.collide(this);
		}
	}
	
	public void renderHealth() {
	}
	
	@Override
	public void render() {
		renderHealth();
		super.render();
	}
	
	public void hurt(Entity e, float damage) {
		health -= damage;
	}
}
