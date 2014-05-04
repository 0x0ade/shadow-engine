package net.fourbytes.shadow.mod;

import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.blocks.BlockType;

/**
 * The ModAPIDefault is the default IModAPI used by
 * Shadow Engine itself. It extends and is not to be
 * confused with IModAPIStandard.
 */
public class ModAPIDefault implements IModAPIStandard {

	@Override
	public String apiName() {
		return "Default Shadow Engine ModAPI";
	}

	@Override
	public String apiAuthor() {
		return "Fourbytes and GitHub contributors";
	}

	@Override
	public String apiVersion() {
		return "Not counted...";
	}

	@Override
	public IMod getModule() {
		return null;
	}

	@Override
	public void preTick() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				((IModStandard) mod).preTick();
			}
		}
	}

	@Override
	public void postTick() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				((IModStandard) mod).postTick();
			}
		}
	}

	@Override
	public void preRender() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				((IModStandard) mod).preRender();
			}
		}
	}

	@Override
	public void postRender() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				((IModStandard) mod).postRender();
			}
		}
	}

	@Override
	public BlockType getTypeBlock(String subtype, float x, float y, Layer layer) {
		BlockType b = null;
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				b = ((IModStandard) mod).getTypeBlock(subtype, x, y, layer);
				if (b != null) {
					break;
				}
			}
		}
		return b;
	}

	@Override
	public boolean generateTile(Level genLevel, int xx, int x, int y, int ln) {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				if (!((IModStandard) mod).generateTile(genLevel, xx, x, y, ln)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void create() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			mod.create();
		}
	}

	@Override
	public void dispose() {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			mod.dispose();
		}
	}

	@Override
	public void initLevelSystems(Level level) {
		for (int i = 0; i < ModManager.mods.size; i++) {
			IMod mod = ModManager.mods.items[i];
			if (mod instanceof IModStandard) {
				((IModStandard) mod).initLevelSystems(level);
			}
		}
	}

}
