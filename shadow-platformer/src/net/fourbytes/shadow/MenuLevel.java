package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.KeyListener;
import net.fourbytes.shadow.Input.TouchPoint;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;

public abstract class MenuLevel extends Level implements KeyListener {
	
	public static class MenuItem {
		public MenuLevel menu;
		public String text;
		public Runnable action;
		public boolean mouseDown = false;
		public Rectangle mouser = new Rectangle();
		public MenuItem(MenuLevel menu, String text, Runnable action) {
			this.menu = menu;
			this.text = text;
			this.action = action;
		}
	}
	
	public Array<MenuItem> items = new Array<MenuItem>(MenuItem.class);
	public MenuLevel parent;
	public MenuItem current;
	public Level bglevel;
	public boolean bgpaused = true;
	public Color dimm = new Color(0f, 0f, 0f, 0.3f);
	public float stepspeed = 0.9f;
	public float step = 0f;
	public int logostep = 0;
	public boolean showtitle = true;
	
	public MenuLevel() {
		this(null);
	}
	
	public MenuLevel(MenuLevel parent) {
		hasvoid = false;
		this.parent = parent;
		if (parent != null) {
			this.bglevel = parent.bglevel;
		}
		Input.keylisteners.add(this);

		lights = null;
		
		System.gc();
	}

	@Override
	public void tick() {
		if (bglevel != null) {
			boolean lastInteract = true;
			if (bglevel.player != null) {
				player = bglevel.player;
				lastInteract = player.canInteract;
				player.canInteract = false;
			}
			bglevel.paused = bgpaused;
			bglevel.tick();
			bglevel.paused = false;
			if (bglevel.player != null) {
				player.canInteract = lastInteract;
			}
		} else {
			player = null;
		}
		if (current == null || !items.contains(current, true)) {
			current = items.items[0];
		}
		
		TouchPoint tp = null;
		for (TouchPoint ttp : Input.touches.values()) {
			if (ttp != null) {
				tp = ttp;
				break;
			}
		}
		if (tp != null) {
			Vector2 newpos = calcMousePos(tp.pos);
			
			float mx = newpos.x;
			float my = newpos.y;
			
			Rectangle r = Garbage.rects.getNext();
			for (MenuItem mi : items) {
				r.set(mi.mouser);
				r.height = -r.height;
				
				if (r.contains(mx, my)) {
					if (tp.button == 0 || Input.isAndroid) {
						mi.mouseDown = true;
					}
					current = mi;
				} else {
					mi.mouseDown = false;
				}
			}
		} else {
			for (MenuItem mi : items) {
				if (mi.mouseDown) {
					current = mi;
					keyDown(Input.enter);
					mi.mouseDown = false;
				}
			}
			
		}
		
		step *= stepspeed;
		logostep++;
	}
	
	protected final static Vector2 oldpos = new Vector2();
	
	public Vector2 calcMousePos(Vector2 apos) {
		oldpos.set(apos);
		Vector2 pos = Garbage.vec2s.getNext();
		pos.set(apos);
		float cx = Shadow.cam.camrec.x;
		float cy = Shadow.cam.camrec.y;
		float mx = (pos.x * (Shadow.vieww/Shadow.dispw)) * Shadow.cam.cam.zoom;
		float my = (pos.y * (Shadow.viewh/Shadow.disph)) * Shadow.cam.cam.zoom;
		float tx = mx + cx;
		float ty = my + cy;
		pos.set(tx, ty);
		return pos;
	}

	public Image logo;
	public Image image;
	public Image dimmimg;
	public BitmapFont font;

	protected boolean omitloop = false;
	
	@Override
	public void renderImpl() {
		boolean largeUI = Options.getBoolean("gfx.large", true);

		if (bglevel != null && !omitloop) {
			omitloop = true;
			bglevel.canRenderImpl = false;
			Shadow.cam.level = false;
			Shadow.cam.renderLevel(bglevel);
			Shadow.cam.level = true;
			bglevel.canRenderImpl = true;
		}
		omitloop = false;
		Rectangle vp = Shadow.cam.camrec;
		if (dimmimg == null) {
			dimmimg = Images.getImage("white");
		}
		dimmimg.setPosition(vp.x, vp.y+vp.height);
		dimmimg.setSize(1f,  -1f);
		dimmimg.setScale(vp.width, vp.height);
		dimmimg.setColor(dimm);
		dimmimg.draw(Shadow.spriteBatch, 1f);
		
		if (largeUI) {
			font = Fonts.light_large;
		} else {
			font = Fonts.light_normal;
		}
		font.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);
		
		float x1 = font.getScaleX();
		float y1 = font.getScaleY();
		
		if (showtitle) {
			if (logo == null) {
				logo = Images.getImage("logo");
			}
			logo.setScale(font.getScaleX(), font.getScaleY());
			float possibruu = MathUtils.sin(logostep / 16f)/8f;
			logo.setPosition(vp.x + vp.width - logo.getWidth()*logo.getScaleX()
					- x1*32f - 0.125f + Shadow.cam.offs.x*2f,
					vp.y - logo.getHeight()*logo.getScaleY()
							- y1*32f + MathUtils.sin(logostep/32f)/8f + possibruu - 0.125f + Shadow.cam.offs.y*2f);
			logo.draw(Shadow.spriteBatch, 1f);
		}

		if (image != null) {
			//Render the "cursor" in between of map / title and font for less render calls.
			//BTW: How does lowering the amount of render calls fix unstable maxSpritesInBatch but also raise it?
			image.setColor(0f, 0f, 0f, 0.5f);
			image.draw(Shadow.spriteBatch, 1f);
			image.setPosition(image.getX() - 0.0825f, image.getY() - 0.0825f);
			image.setColor(1f, 1f, 1f, 1f);
			image.draw(Shadow.spriteBatch, 1f);
		}

		float maxw = 0;
		for (int i = 0; i < items.size; i++) {
			MenuItem mi = items.items[i];
			String txt = mi.text;
			TextBounds tb = font.getBounds(txt);
			if (tb.width > maxw) {
				maxw = tb.width;
			}
		}
		float texth = font.getLineHeight();
		for (int i = 0; i < items.size; i++) {
			MenuItem mi = items.items[i];
			String txt = mi.text;
			TextBounds tb = font.getBounds(txt);
			
			float x = vp.width/2 - maxw;
			x += vp.x + vp.width/2;
			x -= x1*32f;
			x += Shadow.cam.offs.x*1.5f;
			float y = vp.height/2 - (items.size*(-texth) + i*texth);
			y += vp.y + vp.height/2;
			y += y1*48f;
			y += Shadow.cam.offs.y*1.5f;

			font.setColor(0f, 0f, 0f, 0.5f);
			font.draw(Shadow.spriteBatch, txt, x + 0.0825f, y + 0.0825f);
			font.setColor(1f, 1f, 1f, 1f);
			font.draw(Shadow.spriteBatch, txt, x, y);
			
			mi.mouser.set(x, y, tb.width, tb.height);
			
			if (mi == current) {
				if (image == null) {
					TextureRegion reg = Images.split("player", 16, 16)[1][0];
					image = new Image(reg);
				}
				
				float zoomscale = 2f;

				if (largeUI) {
					zoomscale = 4f;
				}
				
				image.setScale(font.getScaleX()*zoomscale, font.getScaleY()*zoomscale);
				image.setPosition(x - 16f*image.getScaleX()*1.5f + 0.0825f, (y+step) - 16f*image.getScaleY() - 3f/16f + 0.0825f);
			}
		}
	}
	
	@Override
	public void keyDown(Key key) {
		if (Shadow.level != this) {
			return;
		}
		if (key == Input.up || key == Input.down) {
			int dir = 0;
			if (key == Input.up) {
				dir = -1;
			}
			if (key == Input.down) {
				dir = 1;
			}
			int index = items.indexOf(current, true);
			int oindex = index;
			if (index == -1) {
				index = 0;
				dir = 0;
			}
			index += dir;
			if (index < 0) {
				index = 0;
				//index = items.size()-1;
			}
			if (index >= items.size) {
				index = items.size-1;
				//index = 0;
			}
			current = items.items[index];
			step += (oindex - index)*0.875f;
		}
		if (key == Input.enter) {
			if (current == null) {
				current = items.items[0];
			}
			if (current != null) {
				Runnable action = current.action;
				if (action != null) {
					action.run();
				}
			}
		}
		if (key == Input.androidBack) {
			if (parent != null) {
				Shadow.level = parent;
			} else if (bglevel != null) {
				Shadow.level = bglevel;
			}
		}
	}
	
	@Override
	public void keyUp(Key key) {
		if (Shadow.level != this) {
			return;
		}
	}
	
}
