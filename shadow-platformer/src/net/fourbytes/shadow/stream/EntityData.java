package net.fourbytes.shadow.stream;

import com.badlogic.gdx.math.Vector2;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.GameObject;

/**
 * EntityData contains all needed {@link Data} to send {@link Entity Entities} thru {@link IStream}s.
 */
public class EntityData extends GameObjectData<Entity> {
	
	public Vector2 movement = new Vector2(0, 0);
	public boolean facingLeft = true;
	public float slowdown = 0.8f;
	public float objgravity = 1f;
	
	public float MAXHEALTH = 1f;
	public float health = 1f;
	
	public EntityData() {
		super();
	}

	@Override
	public void pack0(Entity go) {
		movement.set(go.movement);
		facingLeft = go.facingLeft;
		slowdown = go.slowdown;
		objgravity = go.objgravity;
		
		MAXHEALTH = go.MAXHEALTH;
		health = go.health;
	}

	@Override
	public GameObject unpack() {
		// TODO Auto-generated method stub
		return null;
	}

}
