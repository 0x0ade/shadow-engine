package com.badlogic.gdx.backends.lwjgl;

import java.awt.Canvas;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AsyncRunnableLwjglApplication extends LwjglApplication {
	
	public AsyncRunnableLwjglApplication (ApplicationListener listener, String title, int width, int height, boolean useGL2) {
		super(listener, title, width, height, useGL2);
	}

	public AsyncRunnableLwjglApplication (ApplicationListener listener) {
		super(listener);
	}

	public AsyncRunnableLwjglApplication (ApplicationListener listener, LwjglApplicationConfiguration config) {
		super(listener, config);
	}

	public AsyncRunnableLwjglApplication (ApplicationListener listener, boolean useGL2, Canvas canvas) {
		super(listener, useGL2, canvas);
	}

	public AsyncRunnableLwjglApplication (ApplicationListener listener, LwjglApplicationConfiguration config, Canvas canvas) {
		super(listener, config, canvas);
	}

	public AsyncRunnableLwjglApplication (ApplicationListener listener, LwjglApplicationConfiguration config, LwjglGraphics graphics) {
		super(listener, config, graphics);
	}
	
	@Override
	void mainLoop () {
		Array<LifecycleListener> lifecycleListeners = this.lifecycleListeners;

		try {
			graphics.setupDisplay();
		} catch (LWJGLException e) {
			throw new GdxRuntimeException(e);
		}

		listener.create();
		listener.resize(graphics.getWidth(), graphics.getHeight());
		graphics.resize = false;

		int lastWidth = graphics.getWidth();
		int lastHeight = graphics.getHeight();

		graphics.lastTime = System.nanoTime();
		boolean wasActive = true;
		while (running) {
			Display.processMessages();
			if (Display.isCloseRequested()) exit();

			boolean isActive = Display.isActive();
			if (wasActive && !isActive) { // if it's just recently minimized from active state
				wasActive = false;
				for (LifecycleListener listener : lifecycleListeners) {
					listener.pause();
				}
				listener.pause();
			}
			if (!wasActive && isActive) { // if it's just recently focused from minimized state
				wasActive = true;
				listener.resume();
					for (LifecycleListener listener : lifecycleListeners) {
						listener.resume();
					}
			}

			boolean shouldRender = false;

			if (graphics.canvas != null) {
				int width = graphics.canvas.getWidth();
				int height = graphics.canvas.getHeight();
				if (lastWidth != width || lastHeight != height) {
					lastWidth = width;
					lastHeight = height;
					Gdx.gl.glViewport(0, 0, lastWidth, lastHeight);
					listener.resize(lastWidth, lastHeight);
					shouldRender = true;
				}
			} else {
				graphics.config.x = Display.getX();
				graphics.config.y = Display.getY();
				if (graphics.resize || Display.wasResized() || Display.getWidth() != graphics.config.width
					|| Display.getHeight() != graphics.config.height) {
					graphics.resize = false;
					Gdx.gl.glViewport(0, 0, Display.getWidth(), Display.getHeight());
					graphics.config.width = Display.getWidth();
					graphics.config.height = Display.getHeight();
					if (listener != null) listener.resize(Display.getWidth(), Display.getHeight());
					graphics.requestRendering();
				}
			}

			executedRunnables.clear();
			executedRunnables.addAll(runnables);
			runnables.clear();

			for (int i = 0; i < executedRunnables.size; i++) {
				shouldRender = true;
				executedRunnables.get(i).run(); // calls out to random app code that could do anything ...
			}

			// If one of the runnables set running to false, for example after an exit().
			if (!running) break;

			input.update();
			shouldRender |= graphics.shouldRender();
			input.processEvents();
			if (audio != null) audio.update();

			if (!isActive && graphics.config.backgroundFPS == -1) shouldRender = false;
			int frameRate = isActive ? graphics.config.foregroundFPS : graphics.config.backgroundFPS;
			if (shouldRender) {
				graphics.updateTime();
				listener.render();
				Display.update(false);
			} else {
				// Avoid wasting CPU in an empty loop.
				if (frameRate == -1) frameRate = 10;
				if (frameRate == 0) frameRate = graphics.config.backgroundFPS;
				if (frameRate == 0) frameRate = 30;
			}
			if (frameRate > 0) Display.sync(frameRate);
		}

		for (LifecycleListener listener : lifecycleListeners) {
			listener.pause();
			listener.dispose();
		}
		listener.pause();
		listener.dispose();
		Display.destroy();
		if (audio != null) audio.dispose();
		if (graphics.config.forceExit) System.exit(-1);
	}
	
}
