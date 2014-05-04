package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * AMod (short for AbstractMod) overrides IMod and
 * already follows default implementations for
 * methods given in IModStandard.
 */
public abstract class AMod implements IModStandard {
	
	public AMod() {
	}

	@Override
	public abstract String modName();
	@Override
	public abstract String modAuthor();
	@Override
	public abstract String modVersion();

	@Override
	public void create() {}
	@Override
	public void dispose() {}
	@Override
	public void preTick() {}
	@Override
	public void postTick() {}
	@Override
	public void preRender() {}
	@Override
	public void postRender() {}
	@Override
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer) {return null;}
	@Override
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln) {return true;}
	@Override
	public void initLevelSystems(Level level) {}

}
