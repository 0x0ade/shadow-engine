package net.fourbytes.shadow.stream;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.GameObject;

/**
 * EntityData contains all needed {@link Data} to send {@link Entity Entities} thru {@link IStream}s.
 */
public class EntityData extends GameObjectData<Entity> {

	public EntityData() {
		super();
	}

	@Override
	public GameObjectData<Entity> pack(Entity go) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameObject unpack() {
		// TODO Auto-generated method stub
		return null;
	}

}
