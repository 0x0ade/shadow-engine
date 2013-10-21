package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.genlevel.GenLevel;

public abstract class AMod {
	
	ModContainer cont;
	
	public AMod(ModContainer cont) {
		this.cont = cont;
	}
	
	public abstract String modName();
	public abstract String modAutor();
	public abstract String modVersion();
	
	public void loadResources() {};
	public void preTick() {};
	public void postTick() {};
	public void preRender() {};
	public void postRender() {};
	public Block getTypeBlock(String subtype, float x, float y, Layer layer) {return null;}
	public boolean generateTile(GenLevel genLevel, int xx, int x, int y, int ln) {return true;}
}
