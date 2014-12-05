package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.systems.ISystem;

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
	public void preTick(float delta) {}
	@Override
	public void postTick(float delta) {}
	@Override
	public void preRender(float delta) {}
	@Override
	public void postRender(float delta) {}
	@Override
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer) {return null;}
	@Override
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln) {return true;}
	@Override
	public void initLevelSystems(Level level) {}
	@Override
	public ISystem initLevelSystem(Level level, String name) {return null;}
    @Override
    public ParticleType getParticleType(String typeName) {return null;}
	@Override
	public boolean handleClient(Data data, Object target) {return false;}
	@Override
	public boolean handleServer(Data data, Object target) {return false;}

}
