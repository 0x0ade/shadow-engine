package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.systems.ISystem;

/**
 * IModAPIStandard contains all methods a standard
 * module API (the one used by Shadow Engine itself)
 * should override. It's default implementation is
 * ModAPIDefault.
 */
public interface IModAPIStandard extends IModAPI {

	public void preTick(float delta);
	public void postTick(float delta);
	public void preRender(float delta);
	public void postRender(float delta);
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer);
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln);
	public void create();
	public void dispose();
	public void initLevelSystems(Level level);
	public ISystem initLevelSystem(Level level, String name);
	public ParticleType getParticleType(String typeName);
	public boolean handleClient(Data data, Object target);
	public boolean handleServer(Data data, Object target);

}
