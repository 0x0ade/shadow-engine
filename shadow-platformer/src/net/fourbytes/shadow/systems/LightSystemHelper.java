package net.fourbytes.shadow.systems;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import net.fourbytes.shadow.Shadow;

/**
 * The LightSystemHelper contains public, static variables / methods regarding the
 * LightSystem, mainly it's FrameBuffer.
 */
public final class LightSystemHelper {
	private LightSystemHelper() {
	}

	public static FrameBuffer lightFB;
	public static Rectangle lightFBRect = new Rectangle();
	public static float lightFBFactor = 0.5f;
	public static boolean lightFBClear;
	public static int lightFBSpeed = 2;

	public static FrameBuffer getLightFramebuffer() {
		if (lightFB == null) {
			updateLightBounds();

			lightFB = new FrameBuffer(Pixmap.Format.RGB565, (int) lightFBRect.width, (int) lightFBRect.height, false);
			lightFB.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		}

		return lightFB;
	}

	public static void updateLightBounds() {
		lightFBRect.x = 0f;
		lightFBRect.y = 0f;
		lightFBRect.width = Shadow.dispw*lightFBFactor;
		lightFBRect.height = Shadow.disph*lightFBFactor;
	}
}
