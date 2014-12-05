package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.systems.ISystem;

/**
 * IModStandards (short for InterfaceMod) contains some methods
 * a mod (module) shall override / use by default, such as
 * pre and post tick and render handling, level and level system
 * setup / modification and achievement setup / handling.
 */
public interface IModStandard extends IMod {

	public void preTick(float delta);
	public void postTick(float delta);
	public void preRender(float delta);
	public void postRender(float delta);
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer);
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln);
	public void initLevelSystems(Level level);
	public ISystem initLevelSystem(Level level, String name);
	public ParticleType getParticleType(String typeName);
	public boolean handleClient(Data data, Object target);
	public boolean handleServer(Data data, Object target);

}
