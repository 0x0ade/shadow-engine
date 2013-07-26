package net.fourbytes.shadow.mod;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.badlogic.gdx.files.FileHandle;

public class ModClassLoader extends URLClassLoader {

	public ModClassLoader(FileHandle fh) throws MalformedURLException {
		super(new URL[] {fh.file().toURI().toURL()});
	}
	
}
