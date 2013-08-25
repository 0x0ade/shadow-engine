package net.fourbytes.shadow;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import net.fourbytes.shadow.mod.AMod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Images {
	private final static ObjectMap<String, Image> images = new ObjectMap<String, Image>();
	private final static ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	private final static ObjectMap<String, TextureRegion> textureregs = new ObjectMap<String, TextureRegion>();
	
	public static void addImage(String savename, Image i) {
		images.put(savename, i);
	}
	
	public static Image getImage(String savename, boolean newInstance) {
		Image image = null;
		if (newInstance) {
			image = new Image(getTexture(savename));
		} else {
			image = images.get(savename);
			if (image == null) {
				autoaddImage(savename);
				image = images.get(savename);
			}
		}
		return image;
	}
	
	public static void addTexture(String savename, Texture t) {
		textures.put(savename, t);
	}
	
	public static Texture getTexture(String savename) {
		Texture texture = textures.get(savename);
		if (texture == null) {
			autoaddImage(savename);
			texture = textures.get(savename);
		}
		return texture;
	}
	
	public static void addTextureRegion(String savename, TextureRegion reg) {
		textureregs.put(savename, reg);
	}
	
	public static TextureRegion getTextureRegion(String savename) {
		TextureRegion texturereg = textureregs.get(savename);
		if (texturereg == null) {
			autoaddImage(savename);
			texturereg = textureregs.get(savename);
		}
		return texturereg;
	}
	
	/**
	 * Loads minimum of / only needed resources required for loading screen.
	 */
	public static void loadBasic() {
		//ETC / UI
		addImage("white", "data/white.png");
		
		addImage("logo", "data/logo.png", TextureFilter.Linear, TextureFilter.Linear); //Linear because of artifacts
		
		addImage("bg_blue", "data/bg_blue.png"/*, TextureFilter.Linear, TextureFilter.Linear*/);
		
	}
	
	public static void loadImages() {
		//ETC / UI 
		addImage("bg_polar", "data/bg_polar.png");
		addImage("bg", "data/bg.png");
		
		addImage("cloud", "data/cloud.png");
		addImage("cloud2", "data/cloud2.png");
		addImage("cloud3", "data/cloud3.png");
		
		addImage("moon", "data/moon.png");
		addImage("void", "data/void.png");
		
		//BLOCKS
		//... get added dynamically.
		
		//ENTITIES
		addImage("player", "data/player.png");
	}
	
	public static void autoaddImage(String name) {
		String loadname = name;
		if (name.startsWith("block_")) {
			loadname = name.substring(6);
			loadname = "data/levels/tiles/" + loadname;
		} else {
			loadname = "data/" + loadname;
		}
		if (Gdx.files.internal(loadname+".png").exists()) {
			addImage(name, loadname+".png", TextureFilter.Nearest, TextureFilter.Nearest);
		} else if (Gdx.files.internal(loadname+"block.png").exists()) {
			addImage(name, loadname+"block.png", TextureFilter.Nearest, TextureFilter.Nearest);
		}
	}
	
	public static void addImage(String savename, String loadname) {
		addImage(savename, loadname, TextureFilter.Nearest, TextureFilter.Nearest);
	}
	
	public static void addImage(String savename, String loadname, TextureFilter minFilter, TextureFilter magFilter) {
		try {
			Texture t = new Texture(Gdx.files.internal(loadname), true);
			t.setFilter(minFilter, magFilter);
			addTexture(savename, t);
			addTextureRegion(savename, new TextureRegion(t));
			Image i = new Image(t);
			addImage(savename, i);
		} catch (Throwable t) {
			System.err.println("Loading failed: SN: "+savename+"; LN: "+loadname);
			t.printStackTrace();
		}
	}

	public static void addImageByMod(AMod mod, String savename, String loadname) {
		addImageByMod(mod, savename, loadname, TextureFilter.Nearest, TextureFilter.Nearest);
	}
	
	public static void addImageByMod(AMod mod, String savename, String loadname, TextureFilter minFilter, TextureFilter magFilter) {
		try {
			URL url = mod.getClass().getResource("/assets/"+loadname);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			Pixmap pixmap = new Pixmap(response, 0, response.length);
			Texture t = new Texture(pixmap);
			t.setFilter(minFilter, magFilter);
			addTexture(savename, t);
			addTextureRegion(savename, new TextureRegion(t));
			Image i = new Image(t);
			addImage(savename, i);
		} catch (Throwable t) {
			System.err.println("Loading failed (from mod): SN: "+savename);
			t.printStackTrace();
		}
	}
}
