package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;

public class Camera {
	
	public Background bg;
	public OrthographicCamera cam;

	public static BitmapFont fpsFont = Fonts.light_normal;
	public static boolean fpsBoundsUpdate = true;
	public static TextBounds fpsBoundMain;
	public static String fpsTextMain = "FPS:";

	protected static Rectangle objrec = new Rectangle();
	protected static Image white;

	protected Player player;

	public boolean level = true;
	public boolean firsttick = true;

	public Rectangle camrec = new Rectangle(0f, 0f, 0f, 0f);
	public Vector2 offs = new Vector2(0f, 0f);

	public float inviewf = 2f;

	public static FrameBuffer tmpFB;
	public static FrameBuffer blurFB;
	public static FrameBuffer blurXFB;
	/**
	 * Requires Shadow.resize() to update FB.
	 */
	public static float blursize = 2f;
	public static int blurrad = 4;
	public static float blurdist = 0.1f;

	public Camera() {
		this.cam = new OrthographicCamera(Shadow.vieww, -Shadow.viewh);
		this.cam.position.set(0f, 0f, 0f);
		this.cam.update();
	}
	
	public void resize() {
		cam.viewportWidth = Shadow.vieww;
		cam.viewportHeight = -Shadow.viewh;
		cam.update();
	}
	
	public void render() {
		if (Shadow.level == null) {
			cam.position.set(0f, 0f, 0f);
			cam.zoom = 2f;
			cam.update();

			camrec.set(cam.position.x, cam.position.y, cam.viewportWidth*cam.zoom, -cam.viewportHeight*cam.zoom);
			camrec.x -= camrec.width/2f;
			camrec.y -= camrec.height/2f;

			Shadow.spriteBatch.setProjectionMatrix(cam.combined);
			Shadow.spriteBatch.begin();
			if (bg == null) {
				bg = Background.getDefault();
			}
			Shadow.spriteBatch.disableBlending();
			bg.render();
			Shadow.spriteBatch.enableBlending();
			Image logo = Images.getImage("logo");
			logo.setScale(Shadow.vieww/Shadow.dispw * cam.zoom, -Shadow.viewh/Shadow.disph * cam.zoom);
			logo.setPosition(0f - (logo.getScaleX()*logo.getWidth())/2f, 0f - (logo.getScaleY()*logo.getHeight())/2f);
			logo.draw(Shadow.spriteBatch, 1f);

			if (Shadow.loadstate < 2) {
				Shadow.spriteBatch.setColor(0f, 0f, 0f, 1f);
				Shadow.spriteBatch.draw(Images.getTextureRegion("white"), -Shadow.vieww / 2, Shadow.viewh / 2 + 2, Shadow.vieww, 1);
				Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
				Shadow.spriteBatch.draw(Images.getTextureRegion("white"), -Shadow.vieww / 2, Shadow.viewh / 2 + 2, Shadow.vieww * Shadow.loadtick / Shadow.loadticks[0][Shadow.loadticks[0].length - 1], 1);
			}

			Shadow.spriteBatch.end();

			return;
		}
		if (!(Shadow.level instanceof MenuLevel)) {
			player = Shadow.level.player;
		}
		float goalx;
		float goaly;
		if (player == null) {
			goalx = 0f;
			goaly = 0f;
		} else {
			goalx = player.pos.x + player.rec.width/2f;
			goaly = player.pos.y + player.rec.height/2f;
		}
		if (firsttick) {
			cam.zoom = 1f;
			cam.position.x = goalx;
			cam.position.y = goaly;
			firsttick = false;
		}
		cam.position.x += (goalx-cam.position.x)/15f;
		cam.position.y += (goaly-cam.position.y)/20f;
		offs.set(goalx - cam.position.x, goaly - cam.position.y);
		cam.update();

		camrec.set(cam.position.x, cam.position.y, cam.viewportWidth*cam.zoom, -cam.viewportHeight*cam.zoom);
		camrec.x -= camrec.width/2f;
		camrec.y -= camrec.height/2f;

		Shadow.spriteBatch.setProjectionMatrix(cam.combined);
		Shadow.spriteBatch.maxSpritesInBatch = 0;
		Shadow.spriteBatch.begin();

		if (bg == null) {
			bg = Background.getDefault();
		}
		bg.render();

		renderLevel(Shadow.level);

		if (fpsFont == null) {
			fpsFont = Fonts.light_normal;
			fpsBoundsUpdate = true;
		}
		if (fpsFont != null) {
			//fpsFont.setScale(Shadow.vieww/Shadow.dispw * cam.zoom, -Shadow.viewh/Shadow.disph * cam.zoom);
			//fpsFont.draw(Shadow.spriteBatch, "FPS: "+Shadow.fps, cam.position.x - Shadow.vieww/2 * cam.zoom, cam.position.y - Shadow.viewh/2 * cam.zoom);

			fpsFont.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);

			if (fpsBoundsUpdate) {
				fpsBoundMain = new TextBounds(fpsFont.getBounds(fpsTextMain));
			}

			String fpsTextFPS = Garbage.getStringForInt(Shadow.fps);
			TextBounds fpsBoundFPS = fpsFont.getBounds(fpsTextFPS);

			fpsFont.draw(Shadow.spriteBatch, fpsTextFPS,
					cam.position.x + Shadow.vieww/2f - fpsBoundFPS.width,
					cam.position.y - Shadow.viewh/2f);
			fpsFont.draw(Shadow.spriteBatch, fpsTextMain,
					cam.position.x + Shadow.vieww/2f - fpsBoundMain.width - fpsFont.getSpaceWidth() - fpsBoundFPS.width,
					cam.position.y - Shadow.viewh/2f);
		}

		Shadow.spriteBatch.flush();
		Shadow.spriteBatch.setProjectionMatrix(Input.cam.combined);

		Input.render();

		Shadow.spriteBatch.setProjectionMatrix(cam.combined);

		Shadow.spriteBatch.end();
		//To disable / enable debugging, just add / remove "/*" to / from the beginning of this line.
		/*System.out.println("(Camera) max sprites in batch: "+Shadow.spriteBatch.maxSpritesInBatch);
		System.out.println("(Camera) render calls: "+Shadow.spriteBatch.renderCalls);
		Shadow.spriteBatch.maxSpritesInBatch = 0;
		/*
		 */
	}

	public void renderLevel(Level level) {
		if (!(level instanceof MenuLevel)) {
			if (Options.getBoolean("gfx.blur", true) &&
					//!(level instanceof MenuLevel) && Shadow.level instanceof MenuLevel) {
					!this.level) {
				if (Options.getBoolean("gfx.blur.hq", true)) {
					tmpFB.begin();
				} else {
					blurFB.begin();
				}
				Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			}

			for (Layer ll : level.layers.values()) {
				renderLayer(ll);
			}

			if (level.lights != null) {
				LightSystem.updateLightBounds();
				level.lights.renderFBO();
			}

			if (level.hasvoid) {
				Image levoid = Images.getImage("void");
				objrec.set(camrec.x, level.tiledh - 2, 1024, 1);
				levoid.setScaleY(-1f);
				//i.setPosition(pos.x * Shadow.dispw/Shadow.vieww, pos.y * Shadow.disph/Shadow.viewh);
				levoid.setPosition(objrec.x, objrec.y + objrec.height*2);
				levoid.setSize(objrec.width, objrec.height + objrec.height);
				levoid.draw(Shadow.spriteBatch, 1f);

				Shadow.spriteBatch.disableBlending();
				float fy = level.tiledh;
				if (camrec.y > fy) {
					fy = camrec.y;
				}
				objrec.set(camrec.x, fy, camrec.width, -camrec.height);
				Image lewhite = Images.getImage("white");
				lewhite.setColor(0f, 0f, 0f, 1f);
				lewhite.setPosition(objrec.x, objrec.y);
				lewhite.setSize(1f, 1f);
				lewhite.setScale(objrec.width, objrec.height);
				lewhite.draw(Shadow.spriteBatch, 1f);
				Shadow.spriteBatch.enableBlending();
			}

			if (Options.getBoolean("gfx.blur", true) &&
					!this.level) {
				Shadow.spriteBatch.flush();
				if (Options.getBoolean("gfx.blur.hq", true)) {
					tmpFB.end();
				} else {
					blurFB.end();
				}

				Shadow.spriteBatch.disableBlending();

				if (Options.getBoolean("gfx.blur.hq", true)) {
					blurFB.begin();
					Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
					Shadow.spriteBatch.draw(tmpFB.getColorBufferTexture(),
							camrec.x, camrec.y, camrec.width, camrec.height);
					Shadow.spriteBatch.flush();
					blurFB.end();
				}

				if (Options.getBoolean("gfx.blur.hd", true)) {
					Shadow.spriteBatch.enableBlending();

					float a;

					blurXFB.begin();
					Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
					Shadow.spriteBatch.draw(blurFB.getColorBufferTexture(),
							camrec.x, camrec.y, camrec.width, camrec.height);
					for (float x = blurrad-1f; x > 0; x--) {
						a = 1f - (x / (float)blurrad);
						Shadow.spriteBatch.setColor(1f, 1f, 1f, a / 2f);
						Shadow.spriteBatch.draw(blurFB.getColorBufferTexture(),
								camrec.x - x * blurdist, camrec.y, camrec.width, camrec.height);
						Shadow.spriteBatch.draw(blurFB.getColorBufferTexture(),
								camrec.x + x * blurdist, camrec.y, camrec.width, camrec.height);
					}
					Shadow.spriteBatch.flush();
					blurXFB.end();

					blurFB.begin();
					Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
					Shadow.spriteBatch.draw(blurXFB.getColorBufferTexture(),
							camrec.x, camrec.y, camrec.width, camrec.height);
					for (float y = blurrad-1f; y > 0; y--) {
						a = 1f - (y / (float)blurrad);
						Shadow.spriteBatch.setColor(1f, 1f, 1f, a / 2f);
						Shadow.spriteBatch.draw(blurXFB.getColorBufferTexture(),
								camrec.x, camrec.y - y * blurdist, camrec.width, camrec.height);
						Shadow.spriteBatch.draw(blurXFB.getColorBufferTexture(),
								camrec.x, camrec.y + y * blurdist, camrec.width, camrec.height);
					}
					Shadow.spriteBatch.flush();
					blurFB.end();

					Shadow.spriteBatch.disableBlending();
				}

				Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
				Shadow.spriteBatch.draw(blurFB.getColorBufferTexture(),
						camrec.x, camrec.y, camrec.width, camrec.height);

				Shadow.spriteBatch.enableBlending();
			}

		}

		if (this.level) {
			for (Cursor c : level.cursors) {
				c.preRender();
				c.render();
			}

			if (level.c != null && !Input.isAndroid) {
				level.c.preRender();
				level.c.render();
			}

			level.renderImpl();
		}

	}

	public void renderLayer(Layer l) {
		if (l == null) {
			return;
		}

		Array<GameObject> gos = l.inView;

		boolean prerendered;
		if (Options.getBoolean("gfx.shadows", true)) {
			renderShadows(gos, Options.getBoolean("gfx.shadows.check", false));
			prerendered = true;
		} else {
			prerendered = false;
		}

		boolean multiblended;
		if (Options.getBoolean("gfx.multiblend", true)) {
			renderObjects(gos, false, false, prerendered);
			renderObjects(gos, false, true, prerendered);

			renderObjects(gos, true, false, prerendered);
			renderObjects(gos, true, true, prerendered);

			multiblended = true;
		} else {
			renderObjects(gos, false, prerendered);

			renderObjects(gos, true, prerendered);

			multiblended = false;
		}

		if (multiblended) {
			Shadow.spriteBatch.enableBlending();
		}
	}

	protected void renderShadows(Array<GameObject> gos, boolean doCheck) {
		float ox, oy, ow, oh;
		for (int i = 0; i < gos.size; i++) {
			GameObject go = gos.items[i];

			if (go == null) continue;

			ox = go.pos.x-inviewf;
			oy = go.pos.y-inviewf;
			ow = go.rec.width+inviewf*2f;
			oh = go.rec.height+inviewf*2f;

			if (!(camrec.x < ox + ow && camrec.x + camrec.width > ox &&
					camrec.y < oy + oh && camrec.y + camrec.height > oy)) {
				continue;
			}

			go.preRender();

			if (doCheck && go instanceof Block && !go.blending) {
				int taken = 0;
				Array<Block> blocks;

				blocks = go.layer.get(Coord.get(go.pos.x + 1, go.pos.y));
				if (blocks != null && blocks.size > 0) {
					for (int ii = 0; ii < blocks.size; ii++) {
						Block b = blocks.items[ii];
						if (!b.blending) {
							taken++;
							break;
						}
					}

					blocks = go.layer.get(Coord.get(go.pos.x, go.pos.y + 1));
					if (blocks != null && blocks.size > 0 && taken == 1) {
						for (int ii = 0; ii < blocks.size; ii++) {
							Block b = blocks.items[ii];
							if (!b.blending) {
								taken++;
								break;
							}
						}
					}

					blocks = go.layer.get(Coord.get(go.pos.x + 1, go.pos.y + 1));
					if (blocks != null && blocks.size > 0 && taken == 2) {
						for (int ii = 0; ii < blocks.size; ii++) {
							Block b = blocks.items[ii];
							if (!b.blending) {
								taken++;
								break;
							}
						}
					}

					if (taken == 3) {
						continue;
					}
				}
			}

			Image img = go.images[go.imgIDs[0]]; //TODO Render all images' shadows.
			if (img != null) {
				Shadow.spriteBatch.setColor(0f, 0f, 0f, go.alpha*img.getColor().a*0.5f);
				img.getDrawable().draw(Shadow.spriteBatch, 0.125f + go.pos.x + go.renderoffs.x,
						0.125f + go.pos.y + go.rec.height + go.renderoffs.y,
						go.rec.width + go.renderoffs.width, -go.rec.height + go.renderoffs.height);
			}

		}
	}
	
	protected void renderObjects(Array<GameObject> gos, boolean fgonly, boolean blending, boolean prerendered) {
		float ox, oy, ow, oh;
		boolean blendingSwitched = false;
		for (int i = 0; i < gos.size; i++) {
			GameObject go = gos.items[i];
			if (go == null) continue;
			ox = go.pos.x-inviewf;
			oy = go.pos.y-inviewf;
			ow = go.rec.width+inviewf*2f;
			oh = go.rec.height+inviewf*2f;

			if (!(camrec.x < ox + ow && camrec.x + camrec.width > ox &&
					camrec.y < oy + oh && camrec.y + camrec.height > oy)) {
				continue;
			}
			if (go.blending != blending) {
				continue;
			}
			if (go instanceof Block) {
				if (fgonly && ((Block)go).rendertop == 0x00) {
					continue;
				} else if (!fgonly && ((Block)go).rendertop == 0x01) {
					continue;
				}
			} else if (fgonly) {
					continue;
			}

			if (!prerendered) {
				go.preRender();
			}

			if (!blendingSwitched) {
				if (go.blending) {
					Shadow.spriteBatch.enableBlending();
					blendingSwitched = true;
				} else {
					Shadow.spriteBatch.disableBlending();
					blendingSwitched = true;
				}
			}

			if (go instanceof Block) {
				if (fgonly) {
					((Block)go).renderTop();
				} else {
					go.render();
				}
			} else {
				go.render();
			}
		}
	}

	protected void renderObjects(Array<GameObject> gos, boolean fgonly, boolean prerendered) {
		float ox, oy, ow, oh;
		for (int i = 0; i < gos.size; i++) {
			GameObject go = gos.items[i];
			if (go == null) continue;
			ox = go.pos.x-inviewf;
			oy = go.pos.y-inviewf;
			ow = go.rec.width+inviewf*2f;
			oh = go.rec.height+inviewf*2f;

			if (!(camrec.x < ox + ow && camrec.x + camrec.width > ox &&
					camrec.y < oy + oh && camrec.y + camrec.height > oy)) {
				continue;
			}
			if (go instanceof Block) {
				if (fgonly && ((Block)go).rendertop == 0x00) {
					continue;
				} else if (!fgonly && ((Block)go).rendertop == 0x01) {
					continue;
				}
			} else if (fgonly) {
				continue;
			}

			if (!prerendered) {
				go.preRender();
			}

			if (go instanceof Block) {
				if (fgonly) {
					((Block)go).renderTop();
				} else {
					go.render();
				}
			} else {
				go.render();
			}
		}
	}
	
}

