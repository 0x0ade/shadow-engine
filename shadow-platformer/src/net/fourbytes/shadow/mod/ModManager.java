package net.fourbytes.shadow.mod;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.network.Data;
import net.fourbytes.shadow.systems.ISystem;
import net.fourbytes.shadow.utils.backend.ModLoader;

/**
 * ModManager just holds all mods loaded in the current
 * Shadow Engine application and provides methods to
 * both access them and to also load built-in, later
 * on added mods. Mods are loaded by the backend
 * dependant ModLoader found in the loader field.
 */
public final class ModManager {
	private ModManager() {}
	
	public static Array<IMod> mods = new Array<IMod>(IMod.class);
	public static Array<ModFile> filesLoaded = new Array<ModFile>(ModFile.class);
	public static Array<ModFile> filesIgnored = new Array<ModFile>(ModFile.class);
	public static Array<ModFile> filesFailed = new Array<ModFile>(ModFile.class);
	public static ObjectMap<IMod, ModFile> mapModFile = new ObjectMap<IMod, ModFile>();
	public static ModLoader loader;

	public static ObjectMap<String, IModAPI> apis = new ObjectMap<String, IModAPI>();
	public static IModAPIStandard apiDefault = new ModAPIDefault();

	public static void preTick(float delta) {
		apiDefault.preTick(delta);
	}

	public static void postTick(float delta) {
		apiDefault.postTick(delta);
	}

	public static void preRender(float delta) {
		apiDefault.preRender(delta);
	}

	public static void postRender(float delta) {
		apiDefault.postRender(delta);
	}

	public static BlockType getTypeBlock(String subtype, float x, float y, Layer layer) {
		return apiDefault.getTypeBlock(subtype, x, y, layer);
	}

	public static boolean generateTile(Level genLevel, int xx, int x, int y, int ln) {
		return apiDefault.generateTile(genLevel, xx, x, y, ln);
	}

	public static void create() {
		apiDefault.create();
	}

	public static void dispose() {
		apiDefault.dispose();
	}

	public static void initLevelSystems(Level level) {
		apiDefault.initLevelSystems(level);
	}

	public static ISystem initLevelSystem(Level level, String name) {
		return apiDefault.initLevelSystem(level, name);
	}

	public static ParticleType getParticleType(String typeName) {
		return apiDefault.getParticleType(typeName);
	}

	public static boolean handleClient(Data data, Object target) {
		return apiDefault.handleClient(data, target);
	}

	public static boolean handleServer(Data data, Object target) {
		return apiDefault.handleServer(data, target);
	}

}
