package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Fonts {

	//public static PixmapPacker atlasPacker;
	//public static TextureAtlas atlas;

	public static final String path_bold = "fonts/ubuntu/ubuntu-b.ttf";
	public static final String path_light = "fonts/ubuntu/ubuntu-l.ttf";

	public static BitmapFont bold_small;
	public static BitmapFont bold_normal;
	public static BitmapFont bold_large;

	public static BitmapFont light_small;
	public static BitmapFont light_normal;
	public static BitmapFont light_large;

	public static void load() {
		if (Thread.currentThread() != Shadow.thread) {
			Gdx.app.postRunnable(new Runnable() {
				public void run() {
					load();
				}
			});
			return;
		}

		//atlasPacker = new PixmapPacker(1024, 1024, Pixmap.Format.RGBA8888, 2, true);
		//atlas = atlasPacker.generateTextureAtlas(TextureFilter.MipMapNearestNearest, TextureFilter.Nearest, true);

		FreeTypeFontParameter params = new FreeTypeFontParameter();
		params.size = 0;
		params.flip = false;
		//TODO generate glyphs on our own when using a shared PixmapPacker
		//params.packer = atlasPacker;
		params.packer = null;
		params.minFilter = TextureFilter.Nearest;
		params.magFilter = TextureFilter.Nearest;
		params.genMipMaps = true;

		FreeTypeFontGenerator gen_bold = new FreeTypeFontGenerator(Gdx.files.internal(path_bold));
		params.size = 16;
		bold_small = gen_bold.generateFont(params);
		params.size = 32;
		bold_normal = gen_bold.generateFont(params);
		params.size = 64;
		bold_large = gen_bold.generateFont(params);
		gen_bold.dispose();
		
		FreeTypeFontGenerator gen_light = new FreeTypeFontGenerator(Gdx.files.internal(path_light));
		params.size = 16;
		light_small = gen_light.generateFont(params);
		params.size = 32;
		light_normal = gen_light.generateFont(params);
		params.size = 64;
		light_large = gen_light.generateFont(params);
		gen_light.dispose();
	}
	
}
