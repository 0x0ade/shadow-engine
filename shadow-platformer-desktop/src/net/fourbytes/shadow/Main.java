package net.fourbytes.shadow;

import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;
import com.badlogic.gdx.backends.jglfw.ShadowJglfwApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.fourbytes.shadow.utils.backend.BackendHelper;
import net.fourbytes.shadow.utils.backend.JGLFWBackend;
import net.fourbytes.shadow.utils.backend.LWJGLBackend;

public class Main {
	public static void main(String[] args) {
		boolean useJGLFW = args.length == 1 && args[0].trim().equalsIgnoreCase("jglfw");

		if (useJGLFW) {
			JglfwApplicationConfiguration cfg = new JglfwApplicationConfiguration();
			cfg.title = "shadow-platformer";
			//cfg.useGL30 = false;
			cfg.width = 600;
			cfg.height = 480;

			BackendHelper.backend = new JGLFWBackend(cfg);
			new ShadowJglfwApplication(new Shadow(), cfg);
		} else {
			LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
			cfg.title = "shadow-platformer";
			cfg.useGL30 = false;
			cfg.width = 600;
			cfg.height = 480;

			BackendHelper.backend = new LWJGLBackend(cfg);
			new LwjglApplication(new Shadow(), cfg);
		}
	}
}
