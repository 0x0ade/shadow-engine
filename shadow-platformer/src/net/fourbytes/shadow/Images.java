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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Images {
	private final static ObjectMap<String, Image> images = new ObjectMap<String, Image>();
	private final static ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
	private final static ObjectMap<String, TextureRegion> textureregs = new ObjectMap<String, TextureRegion>();
	
	public static boolean mapTiles = true;
	public final static Array<String> tilemapList = new Array<String>();
	public static Texture tilemap;
	
	public static void addImage(String savename, Image i) {
		images.put(savename, i);
	}
	
	public static Image getImage(String savename, boolean newInstance) {
		Image image = null;
		if (newInstance) {
			image = new Image(getTextureRegion(savename));
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
			addImageToAtlas(name, loadname+".png");
		} else if (Gdx.files.internal(loadname+"block.png").exists()) {
			addImageToAtlas(name, loadname+"block.png");
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
	
	public static void addImageToAtlas(String savename, String loadname) {
		if (!mapTiles) {
			addImage(savename, loadname, TextureFilter.Nearest, TextureFilter.Nearest);
			return;
		}
		
		if (tilemap == null) {
			tilemap = new Texture(16*8, 16*8, Format.RGBA8888);
			tilemap.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}
		
		Pixmap pm = new Pixmap(Gdx.files.internal(loadname));
		
		Texture t = new Texture(pm);
		t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		addTexture(savename, t);
		
		boolean fallback = false;
		
		if (t.getWidth() != 16 || t.getHeight() != 16) {
			System.err.println("Texture has non-tileset-conform size! Falling back...");
			fallback = true;
		}
		
		int regX = tilemapList.size;
		int regY = 0;
		while (!fallback && regX >= tilemap.getWidth()/16) {
			regY++;
			regX--;
		}
		if (regY >= tilemap.getHeight()/16) {
			System.err.println("Dynamic texture atlas too small! Falling back...");
			fallback = true;
		}
		
		if (fallback) {
			System.err.println("Falling back for texture "+savename);
			TextureRegion region = new TextureRegion(t);
			addTextureRegion(savename, region);
			Image i = new Image(region);
			addImage(savename, i);
			
			pm.dispose();
			return;
		}
		
		tilemap.draw(pm, regX*16, regY*16);
		TextureRegion region = new TextureRegion(tilemap, regX*16, regY*16, 16, 16);
		addTextureRegion(savename, region);
		Image i = new Image(region);
		addImage(savename, i);
		tilemapList.add(savename);
		
		pm.dispose();
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
