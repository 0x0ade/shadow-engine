package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.mod.AMod;
import net.fourbytes.shadow.utils.MultiObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public final class Images {
	private Images() {}

	private final static ObjectMap<String, Image> images = new ObjectMap<String, Image>();
	private final static ObjectMap<String, TextureRegion> textureregs = new ObjectMap<String, TextureRegion>();
	
	public static PixmapPacker packer;
	public static TextureAtlas atlas;
	public static Array<String> atlasList = new Array<String>();
	
	public static void addImage(String savename, Image i) {
		images.put(savename, i);
	}
	
	public static Image getImage(String savename) {
		Image image = images.get(savename);
		if (image == null) {
			autoaddImage(savename);
			image = images.get(savename);
		}
		return image;
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

		addImage("logo", "data/logo.png");
	}
	
	public static void loadImages() {
		//ETC / UI
		addImage("light", "data/light.png");

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
			addImage(name, loadname+".png");
		} else if (Gdx.files.internal(loadname+"block.png").exists()) {
			addImage(name, loadname+"block.png");
		}
	}
	
	public static void addImage(String savename, String loadname) {
		try {
			Pixmap pm = new Pixmap(Gdx.files.internal(loadname));
			
			packPixmap(pm, savename);
		} catch (Throwable t) {
			System.err.println("Loading failed: SN: "+savename+"; LN: "+loadname);
			t.printStackTrace();
		}
	}

	public static void addImageByMod(AMod mod, String savename, String loadname) {
		try {
			URL url = mod.getClass().getResource("/assets/"+loadname);
			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[2048];
			int n;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			Pixmap pixmap = new Pixmap(response, 0, response.length);
			packPixmap(pixmap, savename);
		} catch (Throwable t) {
			System.err.println("Loading failed (from mod): SN: "+savename);
			t.printStackTrace();
		}
	}
	
	private static void packPixmap(Pixmap pm, String savename) {
		if (packer == null) {
			packer = new PixmapPacker(1024, 1024, Format.RGBA8888, 2, true);
			atlas = packer.generateTextureAtlas(TextureFilter.MipMapNearestNearest, TextureFilter.Nearest, true);
		}
		packer.pack(savename, pm);
		packer.updateTextureAtlas(atlas, TextureFilter.MipMapNearestNearest, TextureFilter.Nearest, true);
		
		TextureRegion region = atlas.findRegion(savename);
		//region = new TextureRegion((Texture)atlas.getTextures().toArray()[0], 0f, 0f, 1f, 1f);
		addTextureRegion(savename, region);
		Image i = new Image(region);
		addImage(savename, i);
		atlasList.add(savename);
		
		pm.dispose();
	}
	
	/**
	 * Splits an texture region into... umm... guess it.
	 */
	public static TextureRegion[][] split(String savename, int w, int h) {
		return split(getTextureRegion(savename), w, h);
	}

	private static ObjectMap<MultiObject, TextureRegion[][]> splitCache = new ObjectMap<MultiObject, TextureRegion[][]>();
	private static MultiObject tmptrimo = new MultiObject(null, null, null);

	/**
	 * Splits an texture region into... umm... guess it.
	 */
	public static TextureRegion[][] split(TextureRegion reg, int w, int h) {
		tmptrimo.set(reg, w, h);

		TextureRegion[][] got = splitCache.get(tmptrimo);
		if (got != null) {
			return got;
		}

		w = Math.max(Math.max(w, -w), 1);
		h = Math.max(Math.max(h, -h), 1);

		Texture tex = reg.getTexture();

		int subregw = reg.getRegionWidth()/w;
		int subregh = reg.getRegionHeight()/h;

		subregw = Math.max(subregw, -subregw);
		subregh = Math.max(subregh, -subregh);

		TextureRegion[][] regs = new TextureRegion[subregh][];
		int xx = 0;
		int yy = 0;

		for (int y = reg.getRegionY(); y < reg.getRegionY() + reg.getRegionHeight(); y += h) {
			regs[yy] = new TextureRegion[subregw];
			for (int x = reg.getRegionX(); x < reg.getRegionX() + reg.getRegionWidth(); x += w) {
				TextureRegion subreg = new TextureRegion(tex, x, y, w, h);
				regs[yy][xx] = subreg;
				xx++;
				if (xx >= regs[yy].length) {
					break;
				}
			}
			xx = 0;
			yy++;
			if (yy >= regs.length) {
				break;
			}
		}

		MultiObject mo = new MultiObject(reg, w, h);
		splitCache.put(mo, regs);

		return regs;
	}
}
