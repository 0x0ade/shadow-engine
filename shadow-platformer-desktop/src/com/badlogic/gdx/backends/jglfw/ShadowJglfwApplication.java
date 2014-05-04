package com.badlogic.gdx.backends.jglfw;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;

/**
 * ShadowJglfwApplication extends JglfwApplication and is
 * used by Shadow Engine. It currently is used as H4X as long
 * as there is no official OpenAL generator.
 */
public class ShadowJglfwApplication extends JglfwApplication {

	private static JglfwApplicationConfiguration config;

	private OpenALAudio audio;

	private int foregroundFPS, backgroundFPS, hiddenFPS;

	public static int audioDeviceSimultaneousSources = 16;
	public static int audioDeviceBufferCount = 9;
	public static int audioDeviceBufferSize = 512;

	public ShadowJglfwApplication(ApplicationListener listener) {
		this(listener, listener.getClass().getSimpleName(), 640, 480);
	}

	public ShadowJglfwApplication(ApplicationListener listener, String title, int width, int height) {
		this(listener, createConfig(title, width, height));
	}

	public ShadowJglfwApplication(final ApplicationListener listener, final JglfwApplicationConfiguration config) {
		super(listener, ShadowJglfwApplication.config = config);
	}

	private static JglfwApplicationConfiguration createConfig(String title, int width, int height) {
		JglfwApplicationConfiguration config = new JglfwApplicationConfiguration();
		config.title = title;
		config.width = width;
		config.height = height;
		return config;
	}

	/**
	 * Starts the game loop and initializes OpenAL and other H4X dependant
	 * stuff as overriding initialization was impossible... H4X FTW!
	 */
	@Override
	protected void start() {
		LwjglNativesLoader.load();

		audio = new OpenALAudio(audioDeviceSimultaneousSources, audioDeviceBufferCount,
				audioDeviceBufferSize);
		Gdx.audio = audio;

		hiddenFPS = config.hiddenFPS;
		foregroundFPS = config.foregroundFPS;
		backgroundFPS = config.backgroundFPS;

		super.start();
	}

	/** Handles posted runnables, input, audio and rendering for each frame.
	 */
	@Override
	protected void frame() {
		if (!running) return;

		if (executeRunnables()) graphics.requestRendering();

		if (!running) return;

		input.update();

		audio.update();

		long frameStartTime = System.nanoTime();
		int targetFPS = (graphics.isHidden() || graphics.isMinimized()) ? hiddenFPS : //
				(graphics.isForeground() ? foregroundFPS : backgroundFPS);

		if (targetFPS == -1) { // Rendering is paused.
			if (!isPaused) listener.pause();
			isPaused = true;
		} else {
			if (isPaused) listener.resume();
			isPaused = false;
			if (graphics.shouldRender()) render(frameStartTime);
		}

		if (targetFPS != 0) {
			if (targetFPS == -1)
				sleep(100);
			else
				Sync.sync(targetFPS);
		}
	}

	/** Called when an uncaught exception happens in the game loop.
	 * This implementation disposes audio and then does the
	 * usual stuff.
	 */
	protected void exception(Throwable ex) {
		if (audio != null) {
			audio.dispose();
		}

		super.exception(ex);
	}

	protected void end() {
		audio.dispose();

		super.end();
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

}
