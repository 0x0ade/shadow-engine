package net.fourbytes.shadow;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.entities.Mob;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;

public abstract class Entity extends GameObject {

    @IsSaveable
    public Vector2 movement = new Vector2(0, 0);
	@IsSaveable
	public boolean facingLeft = true;
    public Vector2 oldpos;
	public float objgravity = 1f;
	
	@IsSaveable
	public float MAXHEALTH = 1f;
	@IsSaveable
	public float health = 1f;
	
	public Entity(Vector2 position, Layer layer) {
		super(position, layer);
		inithealth(1f);
		
		pixfac = 1;
	}
	
	public void inithealth(float h) {
		MAXHEALTH = h;
		health = h;
	}
	
	public void dead() {
	}
	
	public void tick(float delta) {
		super.tick(delta);
		
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

        movement.x += layer.level.xgravity*objgravity*60f*delta;
        movement.y += layer.level.gravity*objgravity*60f*delta;

        if (oldpos == null) {
            oldpos = new Vector2();
        }
        oldpos.set(pos);

        pos.y += movement.y*60f*delta;
        pos.x += movement.x*60f*delta;

        boolean collided = false;

        if (solid) {
            //Vector<Block> blocks = (Vector<Block>)layer.blocks.clone();
            for (float x = pos.x-4f; x <= pos.x+4f; x++) {
                for (float y = pos.y-4f; y <= pos.y+4f; y++) {
                    Array<Block> blocks = layer.get(Coord.get(x, y));
                    if (blocks != null) {
                        for (int i = 0; i < blocks.size; i++) {
                            Block b = blocks.items[i];
                            if (b == null) continue;
                            if (!b.solid) collide(b, true);
                            else collided = collide(b, false) || collided;
                        }
                    }
                }
            }

            for (int i = 0; i < layer.entities.size; i++) {
                Entity e = layer.entities.items[i];
                if (e == null) continue;
                if (e == this) continue;
                if (!e.solid) collide(e, true);
                else collide(e, true);
            }
        }

        if (!collided) {
            movement.x *= (1f - (slowdown * slowdown)) * (1f - 2f * delta);
        }
    }

    protected static Rectangle er = new Rectangle();
    protected static Rectangle or = new Rectangle();

    protected void calcCollide() {
        float rad = (er.width+er.height)/2f;
        rad *= 0.01f;
        er.set(pos.x + rad, pos.y + rad, rec.width - rad*2f, rec.height - rad*2f);
        if ((er.width+er.height)/2f <= 0.01f) {
            er.set(er.x, er.y, er.width, er.height);
        }
    }

    public boolean collide(GameObject o, boolean canpass) {
        calcCollide();
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

        if (er.overlaps(or)) {
            if (!canpass) {
                if (movement.y > 0) {
                    if (this instanceof Player) {
                        ((Player)this).canJump = ((Player)this).maxJump;
                    }
                    if (this instanceof Mob) {
                        ((Mob)this).canJump = ((Mob)this).maxJump;
                    }
                }

                movement.x *= 1f - slowdown * o.slowdown;

                pos.y = oldpos.y;
                tmpy = movement.y;
                movement.y = 0f;

                calcCollide();
            }
            collide = true;
        }

        if (er.overlaps(or)) {
            if (!canpass) {
                pos.x = oldpos.x;
                movement.x = 0f;
                pos.y += tmpy;

                calcCollide();
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
            collide(o);
        }

        return collide;
    }

    public void collide(GameObject go) {
    }
	
	public void renderHealth() {
	}
	
	@Override
	public void render() {
		renderHealth();
		super.render();
	}
	
	public void hurt(GameObject go, float damage) {
		health -= damage;
	}
	
	public void hit(GameObject go) {
		movement.set(((pos.x + rec.width/2f) - (go.pos.x + go.rec.width/2f)) * 1.25f, ((pos.y + rec.height/2f) - (go.pos.y + go.rec.height/2f)) * 0.625f);
	}
}
