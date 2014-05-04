package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.particles.PixelParticle;

public abstract class GameObject {
	
	public boolean blending = true;
	public float alpha = 1f;
	public transient Layer layer;
	public Vector2 pos = new Vector2(0, 0);
	public Rectangle rec = new Rectangle(0, 0, 0, 0);
	public Rectangle renderoffs = new Rectangle(0, 0, 0, 0);
	public boolean solid = true;
	public Color light = new Color(1f, 1f, 1f, 0f);
	public boolean passSunlight = false;
	public Color tintSunlight = new Color(1f, 1f, 1f, 1f);
	public Color tintDarklight = new Color(0f, 0f, 0f, 1f);
	public int[] imgIDs = {0};
	
	public GameObject(Vector2 pos, Layer layer) {
		this.pos = pos;
		setSize(1f, 1f);
		this.layer = layer;
	}
	
	public void setSize(float w, float h) {
		rec.width = w;
		rec.height = h;
	}

	public boolean imgupdate = true;
	public Image[] images = new Image[1];
	public TextureRegionDrawable[] trds = new TextureRegionDrawable[1];

	public static boolean reuseImage = true;
	public Image getImage(int id) {
		Image img = images[id];
		if (imgupdate || img == null) {
			TextureRegion tex = getTexture(id);
			if (tex == null) {
				System.out.println("("+this+(this instanceof Block?(", "+((Block)this).subtype+")"):")")+
						" getTexture("+id+") == null!");
				tex = Images.getTextureRegion("white");
			}
			if (!reuseImage) {
				img = new Image(tex);
				images[id] = img;
			} else {
				if (img == null) {
					trds[id] = new TextureRegionDrawable(tex);
					img = new Image(trds[id]);
					images[id] = img;
				} else {
					trds[id].setRegion(tex);
					img.setDrawable(trds[id]);
				}
			}
			return img;
		} else {
			return img;
		}
	}
	public abstract TextureRegion getTexture(int id);
	
	public void tick() {
	}
	
	private boolean disposedLayer = false;
	
	public void disposeLayer() {
		if (disposedLayer || layer == null) {
			return;
		}
		disposedLayer = true;

		if (!layer.level.layers.containsValue(layer, true)) {
			layer.level = null;
		}
		
		if (this instanceof Entity) {
			Entity e = (Entity) this;
			if (!layer.entities.contains(e, true)) {
				layer = null;
			}
		} else if (this instanceof Block) {
			Block b = (Block) this;
			if (!layer.blocks.contains(b, true)) {
				layer = null;
			}
		}
	}
	
	public void preRender() {
		for (int id : imgIDs) {
			Image img = getImage(id);
			//i.setPosition(pos.x * Shadow.dispw/Shadow.vieww, pos.y * Shadow.disph/Shadow.viewh);
			img.setPosition(pos.x + renderoffs.x, pos.y + rec.height + renderoffs.y);
			img.setSize(rec.width + renderoffs.width, rec.height + renderoffs.height);
			img.setScaleY(-1f);
			renderCalc(id, img);
		}

		imgupdate = false;
	}
	
	protected Color[] baseColors = new Color[1];
	
	public void renderCalc(int id, Image img) {
		tint(id, img);
	}
	
	public void tint(int id, Image img) {
		if (baseColors[id] == null) {
			baseColors[id] = new Color(img.getColor());
		}
		img.setColor(baseColors[id]);
		if (layer != null) {
			img.getColor().mul(layer.tint);
		}
	}
	
	public static Color tmpc = new Color();
	
	public void render() {
		for (int id : imgIDs) {
			Image img = getImage(id);
			Shadow.spriteBatch.setColor(tmpc.set(img.getColor()).mul(1f, 1f, 1f, alpha));
			img.getDrawable().draw(Shadow.spriteBatch, pos.x + renderoffs.x,
					pos.y + rec.height + renderoffs.y,
					rec.width + renderoffs.width, -rec.height + renderoffs.height);
		}
	}
	
	public int pixfac = 1;
	public int pixdur = 1;
	public static int pixffac = 1;
	
	Color c = new Color(0f, 0f, 0f, 0f);
	Color cc = new Color(0f, 0f, 0f, 0f);

	public Array<PixelParticle> pixelify() {
		int fac = pixfac * pixffac;
		if (fac < 0) {
			fac = 1;
		}
		
		//pixfac = 2; //DEBUG LINE, COMMENT WHOLE LINE OUT IF NOT DEBUGGING!

		Array<PixelParticle> particles = new Array<PixelParticle>();

		for (int id : imgIDs) {
			Image img = getImage(id);
			TextureRegion texreg = getTexture(id);
			Texture tex = texreg.getTexture();
			TextureData texdata = tex.getTextureData();
			if (!texdata.isPrepared()) {
				texdata.prepare();
			}
			Pixmap pixmap = texdata.consumePixmap();

			int tx = texreg.getRegionX();
			int ty = texreg.getRegionY();
			int tw = texreg.getRegionWidth();
			int th = texreg.getRegionHeight();
			float w = rec.width;
			float h = rec.height;
			float pixw = w / tw * fac;
			float pixh = h / th * fac;

			for (int yy = ty; yy < ty + th; yy += fac) {
				for (int xx = tx; xx < tx + tw; xx += fac) {
					int rgba = pixmap.getPixel(xx, yy);
					for (int yyy = 0; yyy < fac; yyy++) {
						for (int xxx = 0; xxx < fac; xxx++) {
							c.set(0f, 0f, 0f, 0f);
							cc.set(0f, 0f, 0f, 0f);
							Color.rgba8888ToColor(c, rgba);
							Color.rgba8888ToColor(cc, pixmap.getPixel(xx + xxx, yy + yyy));
							c.mul(0.5f);
							cc.mul(0.5f);
							c.add(cc);
							rgba = Color.rgba8888(c);
						}
					}
					//System.out.println("X: "+xx+"; Y: "+yy+"; RGBA: "+Integer.toHexString(rgba));
					Color ccc = new Color(0f, 0f, 0f, 0f);
					Color.rgba8888ToColor(ccc, rgba);
					ccc.mul(img.getColor());
					if (c.a < 0.0625f) continue;
					PixelParticle pp = new PixelParticle(new Vector2(pos.x + ((xx - tx) * pixw / fac) + renderoffs.x, pos.y + ((yy - ty) * pixh / fac) + renderoffs.y), layer, 0, pixw, ccc);
					layer.add(pp);
					particles.add(pp);
				}
			}
			if (texdata.disposePixmap()) {
				pixmap.dispose();
			}
		}

		return particles;
	}
	
	public GameObject duplicate() {
		Class<? extends GameObject> c = getClass();
		GameObject clone = null;
		try {
			clone = c.getConstructor(Vector2.class, Layer.class).newInstance(new Vector2(pos), layer);
		} catch (Exception e) {
			try {
				if (this instanceof Block) {
					clone = BlockType.getInstance(((Block)this).subtype, pos.x, pos.y, layer);
					//clone = (GameObject) c.getConstructor(Vector2.class, Layer.class, BlockType.class).newInstance(new Vector2(pos), layer, type);
				}
			} catch (Exception e1) {
				System.err.println("e:");
				e.printStackTrace();
				System.err.println("e1:");
				e1.printStackTrace();
			}
		}
		return clone;
	}
	
	public void collide(Entity e) {
	}
}
