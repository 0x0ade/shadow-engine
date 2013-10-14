package net.fourbytes.shadow;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import net.fourbytes.shadow.map.Converter;

public class ConverterLauncher {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Shadow Engine TMX to SMF converter");
			System.err.println("Version: Don't even ask me.");
			System.err.println("Usage: <process> <input> <output>");
			return;
		}

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "shadow-tmx-to-smf";
		cfg.useGL20 = true;
		cfg.width = 600;
		cfg.height = 480;

		Files files = new LwjglFiles();

		FileHandle input = files.internal(args[0]);
		if (!input.exists()) {
			input = files.absolute(args[0]);
		}
		if (input != null && !input.exists()) {
			System.err.println("Input file doesn't exist internally nor absolutely!");
			return;
		}

		FileHandle output = files.absolute(args[1]);

		Converter converter = new Converter(input, output);

		Converter.list.add(converter);
		Converter.convertOnly = true;

		Shadow.backend = new LWJGLBackend();
		new LwjglApplication(new Shadow(), cfg);
	}
}
