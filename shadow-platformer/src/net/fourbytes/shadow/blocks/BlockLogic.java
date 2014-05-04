package net.fourbytes.shadow.blocks;

import net.fourbytes.shadow.blocks.BlockType.LogicType;

public interface BlockLogic {
	public boolean triggered();
	public void handle(boolean triggered);
	public LogicType getType();
}
