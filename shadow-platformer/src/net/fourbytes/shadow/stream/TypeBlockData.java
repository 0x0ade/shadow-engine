package net.fourbytes.shadow.stream;

import net.fourbytes.shadow.Entity;
import net.fourbytes.shadow.GameObject;
import net.fourbytes.shadow.TypeBlock;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * TypeBlockData contains all needed {@link Data} to send {@link TypeBlock}s and it's {@link BlockType}s thru {@link IStream}s.
 */
public class TypeBlockData extends GameObjectData<TypeBlock> {

	public TypeBlockData() {
		super();
	}

	@Override
	public GameObjectData<TypeBlock> pack(TypeBlock go) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameObject unpack() {
		// TODO Auto-generated method stub
		return null;
	}

}
