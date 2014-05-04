package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * IModAPIStandard contains all methods a standard
 * module API (the one used by Shadow Engine itself)
 * should override. It's default implementation is
 * ModAPIDefault.
 */
public interface IModAPIStandard extends IModAPI {

	public void preTick();
	public void postTick();
	public void preRender();
	public void postRender();
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer);
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln);
	public void create();
	public void dispose();
	public void initLevelSystems(Level level);

}
