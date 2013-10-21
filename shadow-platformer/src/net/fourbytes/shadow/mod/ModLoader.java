package net.fourbytes.shadow.mod;

import com.badlogic.gdx.files.FileHandle;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.genlevel.GenLevel;
import net.fourbytes.shadow.mod.builtin.BuiltinHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLClassLoader;
import java.util.ArrayList;

public final class ModLoader {
	private ModLoader() {}
	
	public static ArrayList<ModContainer> mods = new ArrayList<ModContainer>();
	public static FileHandle folder;
	
	public static void init(FileHandle folder) {
		FileHandle fhblacklist = folder.child("blacklist.txt");
		ArrayList<String> blacklist = new ArrayList<String>();
		if (!fhblacklist.exists()) {
			try {
				fhblacklist.file().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Reader blreader = fhblacklist.reader();
		BufferedReader reader = new BufferedReader(blreader);
		try {
			String line;
			do {
				line = reader.readLine();
				if (line != null) {
					System.out.println("blacklist.txt line: "+line);
					blacklist.add(line);
				}
			} while (line != null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		init(folder, blacklist);
	}
	
	public static void init(FileHandle folder, ArrayList<String> blacklist) {
		ModLoader.folder = folder;
		
		System.out.println("folder: "+folder.path());
		
		for (FileHandle fh : folder.list()) {
			System.out.println("file: "+fh.path());
			if (blacklist.contains(fh.name())) {
				System.out.println("Skippin - blacklisted..");
				continue;
			}
			load(fh, blacklist);
		}
	}
	
	public static ModContainer load(FileHandle fh, ArrayList<String> blacklist) {
		ModContainer cont = null;
		
		if (fh.exists() && !fh.isDirectory() && fh.name().startsWith("mod_") && (fh.extension().toLowerCase().contains("zip") || fh.extension().toLowerCase().contains("jar"))) {
			System.out.println("Seems to be mod..");
			try {
				URLClassLoader ucl = new ModClassLoader(fh);
				cont = new ModContainer(fh, ucl);
				cont.load();
				mods.add(cont);
				System.out.println("true story");
			} catch (Throwable t) {
				if (t instanceof ModLoadFailedException) {
					if (!(t.getCause() instanceof ClassNotFoundException)) {
						t.printStackTrace();
						cont = null;
					}
				}
				System.out.println(".. or not.");
			}
		}
		
		return cont;
	}
	
	public static void initBuiltin() {
		for (Class c : BuiltinHelper.modlist) {
			System.out.println("BuiltIn Mod: "+c);
			try {
				ModContainer cont = new ModContainer(c);
				mods.add(cont);
			} catch (ModLoadFailedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void preTick() {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			mod.preTick();
		}
	}
	
	public static void postTick() {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			mod.postTick();
		}
	}
	
	public static void preRender() {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			mod.preRender();
		}
	}
	
	public static void postRender() {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			mod.postRender();
		}
	}

	public static Block getTypeBlock(String subtype, float x, float y, Layer layer) {
		Block b = null;
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			b = mod.getTypeBlock(subtype, x, y, layer);
			if (b != null) {
				break;
			}
		}
		return b;
	}


	public static boolean generateTile(GenLevel genLevel, int xx, int x, int y, int ln) {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			if (!c.mod.generateTile(genLevel, xx, x, y, ln)) {
				return false;
			}
		}
		return true;
	}
	
	public static void loadResources() {
		for (ModContainer c : mods) {
			AMod mod = c.mod;
			mod.loadResources();
		}
	}

}
