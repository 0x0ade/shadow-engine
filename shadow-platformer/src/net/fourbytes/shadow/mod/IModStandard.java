package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * IModStandards (short for InterfaceMod) contains some methods
 * a mod (module) shall override / use by default, such as
 * pre and post tick and render handling, level and level system
 * setup / modification and achievement setup / handling.
 */
public interface IModStandard extends IMod {

	public void preTick();
	public void postTick();
	public void preRender();
	public void postRender();
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer);
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln);
	public void initLevelSystems(Level level);

}
