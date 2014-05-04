package net.fourbytes.shadow.mod;

import com.badlogic.gdx.files.FileHandle;

/**
 * This class contains the data needed to blacklist
 * a mod or, in the case of Android, uninstall it.
 * <br>
 * <br>
 * For platforms able to load mods from files rather
 * than packages / apps, {@link #fh} is non-null and
 * points to the file the mod has been loaded from.
 * <br>
 * For other platforms, {@link #fh} is null and pkg
 * contains the package name, not the app name.
 * <br>
 * If the mod has been loaded properly, {@link #mod}
 * is the instance of the loaded mod.
 *
 */
public class ModFile {

	public boolean canDelete;

	public String pkg;
	public FileHandle fh;

	public IMod mod;

	public ModFile(String pkg) {
		//Android
		this.pkg = pkg;
		this.canDelete = true;
	}

	public ModFile(FileHandle fh) {
		//Desktop
		this.pkg = fh.name();
		this.fh = fh;
		this.canDelete = false;
	}

	@Override
	public int hashCode() {
		return (canDelete?1024:-1024)+pkg.hashCode()*16+fh.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModFile) {
			ModFile mf = ((ModFile) obj);
			if (!canDelete == mf.canDelete) {
				return false;
			}
			if (pkg == null || !pkg.equals(mf.pkg)) {
				return false;
			}
			if (fh == null || !fh.equals(mf.fh)) {
				return false;
			}
			if (pkg == null || !pkg.equals(mf.pkg)) {
				return false;
			}
		}
		return false;
	}

}
