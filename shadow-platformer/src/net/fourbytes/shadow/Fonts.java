package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Fonts {
	
	public static final String path_bold = "fonts/ubuntu/ubuntu-b.ttf";
	public static final String path_light = "fonts/ubuntu/ubuntu-l.ttf";
	
	public static BitmapFont bold_normal;
	public static BitmapFont bold_large;
	
	public static BitmapFont light_normal;
	public static BitmapFont light_large;
	
	public static void load() {
		TextureFilter tf = (Shadow.isAndroid)?(TextureFilter.Nearest):(TextureFilter.Linear);
		FreeTypeFontGenerator gen_bold = new FreeTypeFontGenerator(Gdx.files.internal(path_bold));
		bold_normal = gen_bold.generateFont(32);
		bold_normal.getRegion().getTexture().setFilter(tf, tf);
		bold_large = gen_bold.generateFont(64);
		bold_large.getRegion().getTexture().setFilter(tf, tf);
		gen_bold.dispose();
		
		FreeTypeFontGenerator gen_light = new FreeTypeFontGenerator(Gdx.files.internal(path_light));
		light_normal = gen_light.generateFont(32);
		light_normal.getRegion().getTexture().setFilter(tf, tf);
		light_large = gen_light.generateFont(64);
		light_large.getRegion().getTexture().setFilter(tf, tf);
		gen_light.dispose();
	}
	
}
