package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.mod.IMod;
import net.fourbytes.shadow.mod.ModFile;
import net.fourbytes.shadow.mod.ModManager;

/**
 * ModLoader is a backend-dependant mod loader using ModManager
 * to manage the loaded mods backend-independently.
 */
public abstract class ModLoader {

	public ModLoader() {
	}

	/**
	 * Searches for mods in given root and excludes files from blacklist loaded from file.
	 */
	public abstract void init(String root);

	/**
	 * Searches for mods in given root and excludes files from the given blacklist.
	 */
	public abstract void init(String root, Array<String> blacklist);

	/**
	 * Loads the given mod from the given path; adds it to the list of failed mods when failed loading.
	 * <br>
	 * When the mod loaded successfully, failed mods get loaded afterwards.
	 */
	public abstract IMod load(ModFile mf);

	/**
	 * Loads the given mod from the given path; adds it to the list of failed mods when failed loading.
	 * <br>
	 * When the mod loaded successfully, failed mods get loaded afterwards.
	 */
	public abstract IMod load(String path);

	/**
	 * Reloads all the mods that failed loading.
	 */
	public void loadFailed() {
		for (int i = 0; i < ModManager.filesFailed.size; i++) {
			ModFile mf = ModManager.filesFailed.items[i];

			System.out.println("Reloading "+mf+" ...");
			load(mf);
		}
	}

	/**
	 * Adds or removes the given modfile from / to the blacklist.
	 */
	public abstract void blacklist(ModFile modfile, boolean blacklist);

	/**
	 * Deletes the given modfile permanently.
	 */
	public abstract void delete(ModFile modfile);
}
