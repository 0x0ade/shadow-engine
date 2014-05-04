package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.ChainClassLoader;
import net.fourbytes.shadow.mod.IMod;
import net.fourbytes.shadow.mod.ModFile;
import net.fourbytes.shadow.mod.ModManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

/**
 * DesktopModLoader is an implementation of ModLoader for
 * the LWJGL and JGLFW (and maybe other desktop) backends.
 */
public class DesktopModLoader extends ModLoader {

	public DesktopModLoader() {
	}

	public String root;

	@Override
	public void init(String root) {
		if (root == null) {
			String path = "";
			try {
				String rawpath = Shadow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				path = URLDecoder.decode(rawpath, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			FileHandle fh = Gdx.files.absolute(path).parent();
			root = fh.path();
		}
		this.root = root;

		FileHandle folder = Gdx.files.absolute(root);

		FileHandle fhbl = folder.child("blacklist.txt");
		Array<String> blacklist = new Array<String>();
		if (!fhbl.exists()) {
			try {
				fhbl.file().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			BufferedReader reader = new BufferedReader(fhbl.reader());
			try {
				String line;
				do {
					line = reader.readLine();
					if (line != null) {
						blacklist.add(line);
					}
				} while (line != null);
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		init(folder.path(), blacklist);
	}

	@Override
	public void init(String root, Array<String> blacklist) {
		FileHandle folder = Gdx.files.absolute(root);

		for (FileHandle fh : folder.list()) {
			if (blacklist.contains(fh.name(), false)) {
				ModManager.filesIgnored.add(new ModFile(fh));
				continue;
			}
			load(fh.path());
		}
	}

	@Override
	public IMod load(ModFile mf) {
		return load(mf.fh.path());
	}

	@Override
	public IMod load(String path) {
		FileHandle fh = Gdx.files.absolute(path);

		IMod mod = null;
		ModFile mf = new ModFile(fh);

		if (fh.exists() && !fh.isDirectory() && fh.name().startsWith("mod_") && (fh.extension().toLowerCase().contains("zip") || fh.extension().toLowerCase().contains("jar"))) {
			System.out.println(fh.name()+" seems to be mod..");
			try {
				//Getting and creating needed class loaders
				ClassLoader ownCL = Shadow.class.getClassLoader();
				ChainClassLoader chainCL = new ChainClassLoader(ownCL);
				URLClassLoader cl = new URLClassLoader(new URL[] {fh.file().toURI().toURL()}, chainCL);
				chainCL.blacklist = new String[] {"net.fourbytes.shadow.mod.Config"};
				if (ModManager.mods.size < 0) {
					IMod pmod = ModManager.mods.items[ModManager.mods.size - 1];
					ClassLoader parentCL = pmod.getClass().getClassLoader().getParent();
					System.out.println(parentCL);
					if (parentCL instanceof ChainClassLoader) {
						ChainClassLoader parentChainCL = (ChainClassLoader) parentCL;
						parentChainCL.chainChild = chainCL;
						chainCL.chainParent = parentChainCL;
					}
				}

				//Getting mod class name
				Class<?> cConfig = cl.loadClass("net.fourbytes.shadow.mod.Config");
				Method method = cConfig.getDeclaredMethod("getModClass");
				Class<?> clazzNT = (Class<?>) method.invoke(null);

				if (IMod.class.isAssignableFrom(clazzNT)) {
					Class<? extends IMod> clazz = clazzNT.asSubclass(IMod.class);

					//Creating mod
					mod = clazz.getConstructor().newInstance();

					//Adding mod to manager
					ModManager.mods.add(mod);
					mf.mod = mod;
					ModManager.filesLoaded.add(mf);
					ModManager.mapModFile.put(mod, mf);

					System.out.println("true story");
				} else {
					System.out.println("it's a trap!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(".. or not.");

				if (ModManager.filesFailed.contains(mf, false)) {
					ModManager.filesFailed.add(mf);
				}
			}
		}

		loadFailed();

		return mod;
	}

	@Override
	public void blacklist(ModFile modfile, boolean blacklist) {
		FileHandle folder = Gdx.files.absolute(root);
		FileHandle fhbl = folder.child("blacklist.txt");

		boolean blacklisted = false;

		Array<String> lines = new Array<String>(String.class);

		BufferedReader br = new BufferedReader(fhbl.reader());
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(modfile.fh.name())) {
					blacklisted = true;
				} else {
					lines.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (!blacklist && blacklisted) {
			BufferedWriter bw = new BufferedWriter(fhbl.writer(false));
			try {
				for (int i = 0; i < lines.size; i++) {
					String line = lines.items[i].trim();
					if (line.length() > 0 && !line.equals(modfile.fh.name())) {
						bw.write(line + "\n");
					}
				}
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (blacklist && !blacklisted) {
			try {
				fhbl.writeBytes((modfile.fh.name() + "\n").getBytes("UTF-8"), true);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void delete(ModFile modfile) {
		if (!modfile.canDelete) {
			throw new RuntimeException("Can not uninstall "+modfile.pkg+" as ModFile.canDelete == false");
		}

		modfile.fh.delete();
	}

}
