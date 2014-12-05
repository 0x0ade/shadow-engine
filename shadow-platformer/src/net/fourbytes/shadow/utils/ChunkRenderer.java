package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.*;

public class ChunkRenderer {

	public OrthographicCamera cam = new OrthographicCamera();
	public int width = 1024;
	public int height = 1024;
	public int ppb = 16;
	public FrameBuffer fbo;
	public Texture tex;

	protected Array<Chunk> render = new Array<Chunk>(Chunk.class);

	protected Array<TextureRegion> regions = new Array<TextureRegion>(TextureRegion.class);
	protected ObjectMap<Chunk, TextureRegion> mapChunkRegion = new ObjectMap<Chunk, TextureRegion>();
	protected ObjectMap<TextureRegion, Chunk> mapRegionChunk = new ObjectMap<TextureRegion, Chunk>();
	protected ObjectIntMap<Chunk> mapChunkId = new ObjectIntMap<Chunk>();

	public ChunkRenderer() {
		setup();
	}

	public void setup() {
		setup(width, height);
	}

	public void setup(int width, int height) {
		this.width = width;
		this.height = height;

		if (fbo != null) {
			fbo.dispose();
		}

		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
		tex = fbo.getColorBufferTexture();
		tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	public void render(Layer l) {
		Shadow.spriteBatch.end();

		render.clear();
		for (int i = 0; i < l.inView.size; i++) {
			GameObject go = l.inView.items[i];
			if (go instanceof Block && !((Block)go).dynamic) {
				Chunk c = go.chunk;
				if (!render.contains(c, true)) {
					preRender(c);
					if (Camera.shadows) {
						renderShadow(c);
					}
					render.add(c);
				}
			}
		}
		for (int i = 0; i < render.size; i++) {
			Chunk c = render.items[i];
			render(c);
		}

		Shadow.spriteBatch.begin();
	}

	public void preRender(Chunk c) {
		TextureRegion reg = mapChunkRegion.get(c);

		if (reg == null) {
			int x = 1;
			int y = 0;
			int w = c.size * ppb;
			int h = c.size * ppb;

			TextureRegion regLast = regions.size > 0 ? regions.peek() : null;

			if (regLast != null) {
				x += regLast.getRegionX() / w;
				y += regLast.getRegionY() / h;
			}

			if (x >= width / w) {
				x = 0;
				y++;
			}
			if (y >= height / h) {
				y = 0;
				TextureRegion regFirst = regions.size > 0 ? regions.first() : null;
				Chunk cFirst = mapRegionChunk.get(regFirst);
				mapRegionChunk.remove(regFirst);
				mapChunkRegion.remove(cFirst);
				mapChunkId.remove(cFirst, 0);
				reg = regFirst;
			}

			if (reg == null) {
				reg = new TextureRegion(tex, x * w, y * h, w, h);
			}

			mapChunkRegion.put(c, reg);
			mapRegionChunk.put(reg, c);
			regions.add(reg);
		}

		if (c.rerender) {
			int x = reg.getRegionX();
			int y = reg.getRegionY();
			int w = reg.getRegionWidth();
			int h = reg.getRegionHeight();

			Shadow.spriteBatch.flush();

			fbo.begin();

			cam.viewportWidth = c.size * (float) width / (float) w;
			cam.viewportHeight = c.size * (float) height / (float) h;
			cam.position.set(c.x * c.size + cam.viewportWidth / 2f - c.size * x / w,
					c.y * c.size + cam.viewportHeight / 2f - c.size * y / h,
					0f);
			cam.update();
			Shadow.spriteBatch.setProjectionMatrix(cam.combined);

			Gdx.gl.glScissor(x, y, w, h);
			Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

			Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


			for (int i = 0; i < c.blocks.size; i++) {
				Block b = c.blocks.items[i];
				if (!b.dynamic) {
					Shadow.spriteBatch.begin();
					b.preRender();
					b.render();
					Shadow.spriteBatch.end();
				}
			}

			Shadow.spriteBatch.flush();

			fbo.end();
			Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
			Shadow.spriteBatch.setProjectionMatrix(Shadow.cam.cam.combined);

			Shadow.spriteCache.beginCache();
			Shadow.spriteCache.add(reg, c.x * c.size, c.y * c.size + c.size, c.size, -c.size);
			mapChunkId.put(c, Shadow.spriteCache.endCache());

			c.rerender = false;
		}
	}

	public void renderShadow(Chunk c) {
		int id = mapChunkId.get(c, 0);

		Shadow.cam.cam.translate(0.125f, 0.125f);
		Shadow.cam.cam.update();

		Shadow.spriteCache.setProjectionMatrix(Shadow.cam.cam.combined);
		Shadow.spriteCache.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);

		ShaderHelper.set("s_colorOverride", 0f, 0f, 0f, 0.5f);

		Shadow.spriteCache.draw(id);

		ShaderHelper.set("s_colorOverride", 1f, 1f, 1f, 1f);

		Shadow.spriteCache.end();

		Shadow.cam.cam.translate(-0.125f, -0.125f);
		Shadow.cam.cam.update();
	}

	public void render(Chunk c) {
		int id = mapChunkId.get(c, 0);

		Shadow.spriteCache.setProjectionMatrix(Shadow.cam.cam.combined);
		Shadow.spriteCache.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);

		Shadow.spriteCache.draw(id);

		Shadow.spriteCache.end();
	}

}
