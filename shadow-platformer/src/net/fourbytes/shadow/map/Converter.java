package net.fourbytes.shadow.map;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Level;

public class Converter {

	protected static FileHandleResolver absoluteResolver = new FileHandleResolver() {
		@Override
		public FileHandle resolve(String fileName) {
			return Gdx.files.absolute(fileName);
		}
	};

	public static Array<Converter> list = new Array<Converter>();
	public static boolean convertOnly = false;

	public FileHandle input;
	public FileHandle output;

	public Converter(FileHandle input, FileHandle output) {
		this.input = input;
		this.output = output;
	}

	public static void convertAll() {
		for (Converter converter : list) {
			converter.convert();
		}
		list.clear();
	}

	public void convert() {
		Level level = new Level();
		try {
			TmxMapLoader tml;
			if (input.type() == Files.FileType.Internal) {
				tml = new TmxMapLoader();
			} else if (input.type() == Files.FileType.Absolute) {
				tml = new TmxMapLoader(absoluteResolver);
			} else {
				throw new ConversionFailedException("Unknown input file LibGDX type!");
			}

			//TODO Custom TmxMapLoader that loads TiledMaps without loading tileset images

			level.initTilED(tml.load(input.path()));
			level.ready = true;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		level.fillLayer(0);

		System.gc();

		ShadowMap shadowMap = ShadowMap.createFrom(level);
		shadowMap.save(output);

		System.out.println("Conversion succeeded!");
	}

	private class ConversionFailedException extends RuntimeException {
		public ConversionFailedException(String message) {
			super(message);
		}

		public ConversionFailedException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
