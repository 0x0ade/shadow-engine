package net.fourbytes.shadow.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.utils.Garbage;

public class DefaultLightSystem implements ILightSystem {

	public TextureRegion white;
	public TextureRegion light;

    public Level level;
	public int tick = 0;

	protected final Color tmpc = new Color(1f, 1f, 1f, 1f);

	public DefaultLightSystem(Level level) {
		this.level = level;
	}

	@Override
	public void render() {
		if (tick < LightSystemHelper.lightFBSpeed-1) {
			tick++;
			return;
		}
		tick = 0;

        Rectangle vp = Shadow.cam.camrec;

		//SELFNOTE: it should be stable enough to draw a light for every object in view.

		if (white == null) {
			white = Images.getTextureRegion("white");
		}

		if (light == null) {
			light = Images.getTextureRegion("light");
		}

		SpriteBatch spriteBatch = Shadow.spriteBatch;
		spriteBatch.setProjectionMatrix(Shadow.cam.cam.combined);
		spriteBatch.maxSpritesInBatch = 0;
		spriteBatch.begin();

		FrameBuffer lightFB = LightSystemHelper.getLightFramebuffer();
		lightFB.begin();

		if (LightSystemHelper.lightFBClear) {
			Gdx.gl.glClearColor(level.globalLight.r, level.globalLight.g, level.globalLight.b, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		} else {
			//spriteBatch.setProjectionMatrix(Garbage.matrix1x1);
			spriteBatch.disableBlending();
			spriteBatch.setColor(level.globalLight.r, level.globalLight.g, level.globalLight.b, 1f);
			//spriteBatch.draw(white, -0.5f, -0.5f, 1f, 1f);
			spriteBatch.draw(white, vp.x, vp.y, vp.width, vp.height);
			spriteBatch.enableBlending();
			//spriteBatch.setProjectionMatrix(Shadow.cam.cam.combined);
		}

		spriteBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);

		for (int i = 0; i < level.mainLayer.inView.size; i++) {
			GameObject go = level.mainLayer.inView.items[i];
			if (go == null) {
				continue;
			}
			if (go.light.a > 0f) {
				drawLight(go.light, go.pos, go.renderoffs, go.rec);
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

    public void drawLight(Color c, Vector2 pos, Rectangle renderoffs, Rectangle rec) {
        Rectangle rect = Garbage.rects.getNext();

        if (renderoffs == null) {
            renderoffs = Garbage.rects.getNext();
            renderoffs.set(0f, 0f, 0f, 0f);
        }

        if (rec == null) {
            rec = Garbage.rects.getNext();
            rec.set(0f, 0f, 0f, 0f);
        }

        tmpc.set(c);
        tmpc.a = 1f;
        Shadow.spriteBatch.setColor(tmpc);

        rect.x = pos.x + renderoffs.x + (rec.width + renderoffs.width)/2f;
        rect.y = pos.y + renderoffs.y + (rec.height - renderoffs.height)/2f;
        rect.width = 7.5f*2f * c.a;
        rect.height = 7.5f*2f * c.a;
        //rect.width = 1f;
        //rect.height = 1f;
        rect.x -= rect.width/2f;
        rect.y -= rect.height/2f;

        Shadow.spriteBatch.draw(light, rect.x, rect.y, rect.width, rect.height);
    }

	@Override
	public void renderFBO() {
		Rectangle vp = Shadow.cam.camrec;
		Shadow.spriteBatch.flush();
		Shadow.spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);

		Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
		Shadow.spriteBatch.draw(LightSystemHelper.getLightFramebuffer().getColorBufferTexture(), vp.x, vp.y, vp.width, vp.height);

		Shadow.spriteBatch.flush();
		Shadow.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

    @Override
    public String getName() {
        return "LightSystem";
    }
}
