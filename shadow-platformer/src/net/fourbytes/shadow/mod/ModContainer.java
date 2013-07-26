package net.fourbytes.shadow.mod;

import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;

import com.badlogic.gdx.files.FileHandle;

public class ModContainer {
	
	FileHandle fh;
	ClassLoader cl;
	AMod mod;
	
	public ModContainer(FileHandle fh, ClassLoader cl) {
		this.fh = fh;
		this.cl = cl;
	}
	
	public ModContainer(Class<? extends AMod> modc) throws ModLoadFailedException {
		try {
			this.mod = modc.getConstructor(this.getClass()).newInstance(this);
		} catch (Exception e) {
			throw new ModLoadFailedException(e);
		}
	}

	public AMod load() throws ModLoadFailedException {
		AMod mod = null;
		try {
			Class c = cl.loadClass("net.fourbytes.shadow.mod.Mod");
			mod = (AMod) c.getConstructor(ModContainer.class).newInstance(this);
		} catch (Exception e) {
			throw new ModLoadFailedException(e);
		}
		this.mod = mod;
		return mod;
	}
	
}
