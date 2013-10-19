package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import net.fourbytes.shadow.utils.ShaderHelper;

public class LightSystem {
	
    public Level level;
	public int speed = 2;
	public int tick = 0;

	public static int maxLights = 64;

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

		Rectangle vp = Shadow.cam.camrec;

		TextureRegion light = Images.getTextureRegion("light");

		SpriteBatch spriteBatch = Shadow.spriteBatch;
		int src = Shadow.spriteBatch.getBlendSrcFunc();
		int dst = Shadow.spriteBatch.getBlendDstFunc();
		spriteBatch.setBlendFunction(GL10.GL_ONE, GL10.GL_ONE);
		spriteBatch.setProjectionMatrix(Shadow.cam.cam.combined);
		Shadow.spriteBatch.maxSpritesInBatch = 0;
		spriteBatch.begin();

		FrameBuffer lightFB = getLightFramebuffer();
		lightFB.begin();

		Gdx.gl.glClearColor(level.globalLight.r, level.globalLight.g, level.globalLight.b, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		int i = 0;
		//SELFNOTE: it should be stable enough to draw a light for every object in view.
		for (GameObject go : level.mainLayer.inView) {
			if (go.light.a > 0f) {
				i++;
				tmpc.set(go.light);
				tmpc.a = 1f;
				spriteBatch.setColor(tmpc);

				Garbage.rect.x = go.pos.x + go.renderoffs.x + (go.rec.width + go.renderoffs.width)/2f;
				Garbage.rect.y = go.pos.y + go.renderoffs.y + (go.rec.height - go.renderoffs.height)/2f;
				Garbage.rect.width = 5f*2f * go.light.a;
				Garbage.rect.height = 5f*2f * go.light.a;
				//Garbage.rect.width = 1f;
				//Garbage.rect.height = 1f;
				Garbage.rect.x -= Garbage.rect.width/2f;
				Garbage.rect.y -= Garbage.rect.height/2f;

				spriteBatch.draw(light, Garbage.rect.x, Garbage.rect.y, Garbage.rect.width, Garbage.rect.height);

				if (i > maxLights) {
					System.out.println("Amount of lights in viewport reached limit ("+maxLights+")");
					System.out.println("Stopping drawing lights to lightmap...");
					break;
				}
			}
		}

		spriteBatch.flush();
		lightFB.end();

		spriteBatch.end();
		spriteBatch.setBlendFunction(src, dst);

		/*//To disable / enable debugging, just add / remove "/*" to / from the beginning of this line.
		System.out.println("(LightSystem) max sprites in batch: "+Shadow.spriteBatch.maxSpritesInBatch);
		System.out.println("(LightSystem) render calls: "+Shadow.spriteBatch.renderCalls);
		Shadow.spriteBatch.maxSpritesInBatch = 0;
		/*
		 */
	}

	//Lightmap stuff

	public static FrameBuffer lightFB;
	public static Rectangle lightFBRect = new Rectangle();
	public static float lightFBFactor = 0.5f;
	public static int lightTexID = 1;

	public final static FrameBuffer getLightFramebuffer() {
		if (lightFB == null) {
			updateLightBounds();

			lightFB = new FrameBuffer(Pixmap.Format.RGB565, (int) lightFBRect.width, (int) lightFBRect.height, false);
			lightFB.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

			lightFB.getColorBufferTexture().bind(lightTexID);
			ShaderHelper.getShader("light").begin();
			ShaderHelper.getShader("light").setUniformi("textureLight", lightTexID);
			ShaderHelper.getShader("light").end();
			Gdx.gl.glActiveTexture(GL10.GL_TEXTURE0);
		}

		return lightFB;
	}

	public final static void updateLightBounds() {
		lightFBRect.x = 0f;
		lightFBRect.y = 0f;
		lightFBRect.width = Shadow.dispw*lightFBFactor;
		lightFBRect.height = Shadow.disph*lightFBFactor;
	}

}
