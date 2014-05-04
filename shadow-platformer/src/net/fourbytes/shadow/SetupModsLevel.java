package net.fourbytes.shadow;

import net.fourbytes.shadow.mod.IMod;
import net.fourbytes.shadow.mod.ModFile;
import net.fourbytes.shadow.mod.ModManager;

public class SetupModsLevel extends MenuLevel {

	public ModFile modfile;
	public boolean blacklist = false;

	public SetupModsLevel() {
		this(null, null);
	}

	public SetupModsLevel(MenuLevel parent) {
		this(parent, null);
	}

	public SetupModsLevel(MenuLevel parent, ModFile modfile) {
		super(parent);
		showtitle = false;

		this.modfile = modfile;
		
		refresh();
		
		ready = true;
	}

	public SetupModsLevel(MenuLevel parent, boolean blacklist) {
		super(parent);
		showtitle = false;

		this.blacklist = blacklist;

		refresh();

		ready = true;
	}

	protected void refresh() {
		removeMenuItems();
		if (modfile == null) {
			if (blacklist) {
				addMenuItemsListBlacklist();
			} else {
				addMenuItemsListActive();
			}
		} else {
			addMenuItemsMod();
		}
	}

	protected void addMenuItemsListActive() {
		for (int i = 0; i < ModManager.filesLoaded.size; i++) {
			ModFile mf = ModManager.filesLoaded.items[i];
			items.add(getMenuItemFor(mf));
		}
		if (ModManager.filesIgnored.size > 0) {
			items.add(getMenuItemBlacklist());
		}
		items.add(getMenuItemBack());
	}

	protected void addMenuItemsListBlacklist() {
		for (int i = 0; i < ModManager.filesIgnored.size; i++) {
			ModFile mf = ModManager.filesIgnored.items[i];
			items.add(getMenuItemFor(mf));
		}
		items.add(getMenuItemBack());
	}

	protected void addMenuItemsMod() {
		final boolean blacklisted = modfile.mod==null;

		items.add(new MenuItem(this, modfile.pkg, new Runnable(){public void run(){
		}}));

		items.add(new MenuItem(this, (blacklisted?"Remove from ":"Add to ")+"blacklist",
				new Runnable(){public void run(){
					ModManager.loader.blacklist(modfile, !blacklisted);

					if (blacklisted) {
						ModManager.filesIgnored.removeValue(modfile, true);
						IMod mod = ModManager.loader.load(modfile.fh==null?modfile.pkg:modfile.fh.path());
						mod.create();
					} else {
						modfile.mod.dispose();
						ModManager.mapModFile.remove(modfile.mod);
						modfile.mod = null;
						ModManager.filesLoaded.removeValue(modfile, true);
						ModManager.filesIgnored.add(modfile);
					}

					Shadow.level = parent;
					if (parent instanceof SetupModsLevel) {
						((SetupModsLevel) parent).refresh();
					}
		}}));

		if (modfile.canDelete) {
			items.add(new MenuItem(this, "Delete",
					new Runnable(){public void run(){
						modfile.mod.dispose();
						ModManager.mapModFile.remove(modfile.mod);
						modfile.mod = null;
						ModManager.filesLoaded.removeValue(modfile, true);
						ModManager.filesIgnored.add(modfile);

						ModManager.loader.delete(modfile);

						Shadow.level = parent;
						if (parent instanceof SetupModsLevel) {
							((SetupModsLevel) parent).refresh();
						}
			}}));
		}

		items.add(getMenuItemBack());
	}

	protected void removeMenuItems() {
		items.clear();
	}

	protected MenuItem getMenuItemFor(final ModFile mf) {
		return new MenuItem(this,
				mf.mod==null?mf.pkg:mf.mod.modName(),
				new Runnable(){public void run(){
					Shadow.level = new SetupModsLevel(SetupModsLevel.this, mf);
				}});
	}

	protected MenuItem getMenuItemBlacklist() {
		return new MenuItem(this, "Show blacklisted mods",
				new Runnable(){public void run(){
					Shadow.level = new SetupModsLevel(SetupModsLevel.this, true);
				}});
	}

	protected MenuItem getMenuItemBack() {
		return new MenuItem(this, "Back",
				new Runnable(){public void run(){
					Shadow.level = parent;
					if (parent instanceof SetupModsLevel) {
						((SetupModsLevel) parent).refresh();
					}
				}});
	}

}
