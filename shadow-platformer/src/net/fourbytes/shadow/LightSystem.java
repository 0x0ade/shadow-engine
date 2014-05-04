package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import net.fourbytes.shadow.utils.Garbage;

public class LightSystem {
	
    public Level level;
	public int speed = 2;
	public int tick = 0;

	public static int maxLights = 96;

	protected final static Color tmpc = new Color(1f, 1f, 1f, 1f);

	public LightSystem(Level level) {
		this.level = level;
	}

	public void render() {
		if (tick < speed-1) {
			tick++;
			return;
		}
		tick = 0;

		//SELFNOTE: it should be stable enough to draw a light for every object in view.

		TextureRegion light = Images.getTextureRegion("light");

		SpriteBatch spriteBatch = Shadow.spriteBatch;
		spriteBatch.setProjectionMatrix(Shadow.cam.cam.combined);
		spriteBatch.maxSpritesInBatch = 0;
		spriteBatch.begin();

		FrameBuffer lightFB = getLightFramebuffer();
		lightFB.begin();
		spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);

		Gdx.gl.glClearColor(level.globalLight.r, level.globalLight.g, level.globalLight.b, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		int i = 0;
		Rectangle rect = Garbage.rects.getNext();
		for (int ii = 0; ii < level.mainLayer.inView.size; ii++) {
			GameObject go = level.mainLayer.inView.items[ii];
			if (go == null) {
				continue;
			}
			if (go.light.a > 0f) {
				i++;
				tmpc.set(go.light);
				tmpc.a = 1f;
				spriteBatch.setColor(tmpc);

				rect.x = go.pos.x + go.renderoffs.x + (go.rec.width + go.renderoffs.width)/2f;
				rect.y = go.pos.y + go.renderoffs.y + (go.rec.height - go.renderoffs.height)/2f;
				rect.width = 7.5f*2f * go.light.a;
				rect.height = 7.5f*2f * go.light.a;
				//rect.width = 1f;
				//rect.height = 1f;
				rect.x -= rect.width/2f;
				rect.y -= rect.height/2f;

				spriteBatch.draw(light, rect.x, rect.y, rect.width, rect.height);

				//if (i > maxLights) {
					//System.out.println("Amount of lights in viewport reached limit ("+maxLights+")");
					//System.out.println("Stopping drawing lights to lightmap...");
					//break;
				//}
			}
		}

		spriteBatch.flush();
		lightFB.end();

		spriteBatch.end();
		spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		/*//To disable / enable debugging, just add / remove "/*" to / from the beginning of this line.
		System.out.println("(LightSystem) max sprites in batch: "+Shadow.spriteBatch.maxSpritesInBatch);
		System.out.println("(LightSystem) render calls: "+Shadow.spriteBatch.renderCalls);
		Shadow.spriteBatch.maxSpritesInBatch = 0;
		/*
		 */
	}

	public void renderFBO() {
		Rectangle vp = Shadow.cam.camrec;
		Shadow.spriteBatch.flush();
		Shadow.spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);

		Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
		Shadow.spriteBatch.draw(getLightFramebuffer().getColorBufferTexture(), vp.x, vp.y, vp.width, vp.height);

		Shadow.spriteBatch.flush();
		Shadow.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	//Lightmap stuff

	public static FrameBuffer lightFB;
	public static Rectangle lightFBRect = new Rectangle();
	public static float lightFBFactor = 0.5f;

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
