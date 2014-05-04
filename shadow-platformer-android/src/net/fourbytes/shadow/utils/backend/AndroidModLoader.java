package net.fourbytes.shadow.utils.backend;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import dalvik.system.PathClassLoader;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.ChainClassLoader;
import net.fourbytes.shadow.mod.IMod;
import net.fourbytes.shadow.mod.ModFile;
import net.fourbytes.shadow.mod.ModManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * AndroidModLoader is an implementation of ModLoader for
 * the Android backend.
 */
public class AndroidModLoader extends ModLoader {

	public AndroidModLoader() {
	}

	public static String MOD_META_KEY = "shadowmodule";

	@Override
	public void init(String root) {
		FileHandle fhbl = Shadow.getDir(null).child("blacklist.txt");
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

		init(null, blacklist);
	}

	@Override
	public void init(String root, Array<String> blacklist) {
		PackageManager pm = ((AndroidApplication)Gdx.app).getPackageManager();
		if (pm == null) {
			return;
		}

		//TODO Find out whether META_DATA can be replaced with simple 0
		List<PackageInfo> rawpkgs = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		for (PackageInfo pkg : rawpkgs) {
			String packageName = pkg.packageName;
			if (blacklist != null && blacklist.contains(packageName, false)) {
				ModManager.filesIgnored.add(new ModFile(packageName));
				continue;
			}
			load(packageName);
		}

	}

	@Override
	public IMod load(ModFile mf) {
		return load(mf.pkg);
	}

	@Override
	public IMod load(String path) {
		PackageManager pm = ((AndroidApplication)Gdx.app).getPackageManager();
		if (pm == null) {
			return null;
		}

		IMod mod = null;
		ModFile mf = new ModFile(path);

		try {
			PackageInfo pkg = pm.getPackageInfo(path, PackageManager.GET_META_DATA);
			ApplicationInfo app = pkg.applicationInfo;
			Bundle metaData = app.metaData;
			if (app.enabled && metaData != null && metaData.containsKey(MOD_META_KEY)) {
				System.out.println(path+" is a great module candidate...");
				String clazzLoadName = metaData.getString(MOD_META_KEY);
				String pathAPK = app.publicSourceDir;
				String pathLIB = app.nativeLibraryDir;

				System.out.println("APK: \""+pathAPK+"\"");
				System.out.println("LIB: \""+pathLIB+"\"");

				//Get new APK class loader using own class loader wrapped in ChainClassLoader as parent.
				//History:
				//Own CL didn't work, own parent CL did, but with different class instances (not crashing JVM)
				//Custom worked partially, at some point failed as own did
				//Actually, JVM crashed with own CL due to conflicting class instances
				//Fixed by changing the dependency structure in IntelliJ and using the own CL again.
				//Getting and creating needed class loaders
				ClassLoader ownCL = Shadow.class.getClassLoader();
				ChainClassLoader chainCL = new ChainClassLoader(ownCL);
				PathClassLoader cl = new PathClassLoader(pathAPK, pathLIB, getClass().getClassLoader());
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

				Class<?> clazzNT = cl.loadClass(clazzLoadName);
				if (IMod.class.isAssignableFrom(clazzNT)) {
					Class<? extends IMod> clazz = clazzNT.asSubclass(IMod.class);

					System.out.println("So far so good... let's load the class.");

					//Creating mod
					mod = clazz.getConstructor().newInstance();
					System.out.println(mod.getClass().getClassLoader().getParent());

					//Adding mod to manager
					ModManager.mods.add(mod);
					mf.mod = mod;
					ModManager.filesLoaded.add(mf);
					ModManager.mapModFile.put(mod, mf);

					System.out.println("Such ClassLoader! Very Android! WOW!");
				} else {
					System.out.println("... the cake is a lie... or from another dimension. Either of that.");
				}
			}
			/*
			if (metaData != null) {
				System.out.println("app: "+path);
				for (String key : metaData.keySet()) {
					System.out.println("key: "+key);
					System.out.println("raw: "+(metaData.get(key).toString()));
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();

			if (ModManager.filesFailed.contains(mf, false)) {
				ModManager.filesFailed.add(mf);
			}
		}

		loadFailed();

		return mod;
	}

	@Override
	public void blacklist(ModFile modfile, boolean blacklist) {
		FileHandle fhbl = Shadow.getDir(null).child("blacklist.txt");

		boolean blacklisted = false;

		Array<String> lines = new Array<String>(String.class);

		BufferedReader br = new BufferedReader(fhbl.reader());
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals(modfile.pkg)) {
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
					if (line.length() > 0 && !line.equals(modfile.pkg)) {
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
				fhbl.writeBytes((modfile.pkg + "\n").getBytes("UTF-8"), true);
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
		Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"+modfile.pkg));
		((AndroidApplication)Gdx.app).startActivity(intent);
	}

}
