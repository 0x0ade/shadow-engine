package net.fourbytes.shadow;

import net.fourbytes.shadow.Shadow;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "shadow-platformer";
		cfg.useGL20 = true;
		cfg.width = 600;
		cfg.height = 480;
		
		new LwjglApplication(new Shadow(), cfg);
	}
}
