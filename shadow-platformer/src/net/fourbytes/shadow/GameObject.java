package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.systems.IParticleManager;

import java.lang.reflect.Constructor;

public abstract class GameObject {

    private boolean idSet = false;
    private long id = 0L;

    public final long getID() {
        if (!idSet) {
            setID(Shadow.rand.nextLong());
            return id;
        }

        return id;
    }

    public final void setID(long id) {
        idSet = true;

        if (layer != null && layer.level != null) {
            layer.level.goIDMap.remove(this.id);
            layer.level.goIDMap.put(id, this);
        }

        this.id = id;
    }

	public boolean blending = true;
	public float alpha = 1f;
	public Layer layer;
	public Chunk chunk;
	public Vector2 pos = new Vector2(0, 0);
    public Rectangle rec = new Rectangle(0, 0, 0, 0);
	public Rectangle renderoffs = new Rectangle(0, 0, 0, 0);
    public boolean solid = true;
    public float slowdown = 0.7f;
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
		rec.set(0f, 0f, w, h);
	}

	public boolean imgupdate = true;
	public boolean texupdate = true;
	public Image[] images = new Image[1];
	public TextureRegionDrawable[] trds = new TextureRegionDrawable[1];

	public static boolean reuseImage = true;
	public Image getImage(int id) {
		Image img = images[id];
		if (texupdate || img == null) {
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
	
	public void tick(float delta) {
	}

	public void frame(float delta) {
	}

	public void preRender() {
		if (imgupdate || texupdate) {
			for (int id : imgIDs) {
				Image img = getImage(id);
				//i.setPosition(pos.x * Shadow.dispw/Shadow.vieww, pos.y * Shadow.disph/Shadow.viewh);
				img.setPosition(pos.x + renderoffs.x, pos.y + renderoffs.y);
				img.setSize(rec.width + renderoffs.width, rec.height + renderoffs.height);
				img.setScaleY(-1f);
				renderCalc(id, img);
			}
		}

		imgupdate = false;
		texupdate = false;
	}

	public Color[] baseColors = new Color[1];

	public void renderCalc(int id, Image img) {
		tint(id, img);
	}
	
	public void tint(int id, Image img) {
		Color c = img.getColor();

		if (baseColors[id] == null) {
			baseColors[id] = new Color(c);
		}
		c.set(baseColors[id]);
		if (layer != null) {
			c.mul(layer.tint);
		}
	}
	
	public void render() {
		for (int id : imgIDs) {
			Image img = getImage(id);
			Color imgc = img.getColor();
			Shadow.spriteBatch.setColor(imgc.r, imgc.g, imgc.b, imgc.a * alpha);
			img.getDrawable().draw(Shadow.spriteBatch, pos.x + renderoffs.x,
					pos.y + rec.height + renderoffs.y,
					rec.width + renderoffs.width, -rec.height + renderoffs.height);
		}
	}
	
	public int pixfac = 1;
	public static int pixffac = 1;
	
	Color c = new Color(0f, 0f, 0f, 0f);
	Color cc = new Color(0f, 0f, 0f, 0f);
	Color ccc = new Color(0f, 0f, 0f, 0f);
	Vector2 ppos = new Vector2();

	public Array<Particle> pixelify() {
		int fac = pixfac * pixffac;
		if (fac < 0) {
			fac = 1;
		}
		
		//pixfac = 2; //DEBUG LINE, COMMENT WHOLE LINE OUT IF NOT DEBUGGING!

		Array<Particle> particles = new Array<Particle>();

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
					Color.rgba8888ToColor(ccc, rgba);
					ccc.mul(img.getColor());
					if (c.a < 0.0625f) continue;

					ppos.set(pos.x + ((xx - tx) * pixw / fac) + renderoffs.x, pos.y + ((yy - ty) * pixh / fac) + renderoffs.y);
					Particle pp = layer.level.systems.get(IParticleManager.class).create("PixelParticle", ppos, layer, ccc, pixw, 0);
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
			Constructor<? extends GameObject> constr = c.getConstructor(Vector2.class, Layer.class);
			constr.setAccessible(true);
			clone = constr.newInstance(pos, layer);
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
