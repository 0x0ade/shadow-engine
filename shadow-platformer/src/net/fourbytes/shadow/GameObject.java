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
import com.badlogic.gdx.utils.IntMap;
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
	public IntMap<Image> images = new IntMap<Image>();
	public IntMap<Texture> textures = new IntMap<Texture>();
	public IntMap<TextureRegionDrawable> trds = new IntMap<TextureRegionDrawable>();

	public static boolean reuseImage = true;
	public Image getImage(int id) {
		if (imgupdate || images.get(id) == null) {
			if (!reuseImage) {
				Image img = new Image(getTexture(id));
				images.put(id, img);
			} else {
				if (images.get(id) == null) {
					trds.put(id, new TextureRegionDrawable(getTexture(id)));
					images.put(id, new Image(trds.get(id)));
				} else {
					trds.get(id).setRegion(getTexture(id));
					images.get(id).setDrawable(trds.get(id));
				}
			}
			return images.get(id);
		} else {
			return images.get(id);
		}
	}
	public abstract TextureRegion getTexture(int id);
	
	public void tick() {
	}
	
	private boolean disposedLayer = false;
	
	public final void disposeLayer() {
		if (disposedLayer) {
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
		}
		
		if (this instanceof Block) {
			Block b = (Block) this;
			if (!layer.blocks.contains(b, true)) {
				layer = null;
			}
		}
	}
	
	public void preRender() {
		for (int i = 0; i < imgIDs.length; i++) {
			int id = imgIDs[i];
			Image img = getImage(id);
			//i.setPosition(pos.x * Shadow.dispw/Shadow.vieww, pos.y * Shadow.disph/Shadow.viewh);
			img.setPosition(pos.x + renderoffs.x, pos.y + rec.height + renderoffs.y);
			img.setSize(rec.width + renderoffs.width, rec.height + renderoffs.height);
			img.setScaleY(-1f);
			renderCalc(id, img);
		}

		imgupdate = false;
	}
	
	protected IntMap<Color> baseColors = new IntMap<Color>();
	
	public void renderCalc(int id, Image img) {
		tint(id, img);
	}
	
	public void tint(int id, Image img) {
		if (baseColors.get(id) == null) {
			baseColors.put(id, new Color(img.getColor()));
		}
		img.setColor(baseColors.get(id));
		img.getColor().mul(layer.tint);
	}
	
	public static Color tmpc = new Color();
	
	public void render() {
		for (int i = 0; i < imgIDs.length; i++) {
			int id = imgIDs[i];
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
	Color ccc = new Color(0f, 0f, 0f, 0f);
	
	public Array<PixelParticle> pixelify() {
		int fac = pixfac * pixffac;
		if (fac < 0) {
			fac = 1;
		}
		
		//pixfac = 2; //DEBUG LINE, COMMENT WHOLE LINE OUT IF NOT DEBUGGING!

		Array<PixelParticle> particles = new Array<PixelParticle>();

		for (int i = 0; i < imgIDs.length; i++) {
			int id = imgIDs[i];

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
			float pixw = w/tw * fac;
			float pixh = h/th * fac;

			for (int yy = ty; yy < ty+th; yy+=fac) {
				for (int xx = tx; xx < tx+tw; xx+=fac) {
					int rgba = pixmap.getPixel(xx, yy);
					for (int yyy = 0; yyy < fac; yyy++) {
						for (int xxx = 0; xxx < fac; xxx++) {
							c.set(0f, 0f, 0f, 0f);
							cc.set(0f, 0f, 0f, 0f);
							Color.rgba8888ToColor(c, rgba);
							Color.rgba8888ToColor(cc, pixmap.getPixel(xx+xxx, yy+yyy));
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
					PixelParticle pp = new PixelParticle(new Vector2(pos.x+((xx-tx)*pixw/fac)+renderoffs.x, pos.y+((yy-ty)*pixh/fac)+renderoffs.y), layer, 0, pixw, ccc);
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
		Class c = getClass();
		GameObject clone = null;
		try {
			clone = (GameObject) c.getConstructor(Vector2.class, Layer.class).newInstance(new Vector2(pos), layer);
		} catch (Exception e) {
			try {
				BlockType type = ((TypeBlock)this).type;
				clone = BlockType.getInstance(type.subtype, pos.x, pos.y, layer);
				//clone = (GameObject) c.getConstructor(Vector2.class, Layer.class, BlockType.class).newInstance(new Vector2(pos), layer, type);
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
