package net.fourbytes.shadow;

import java.util.Vector;

import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.KeyListener;
import net.fourbytes.shadow.Input.TouchPoint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public abstract class MenuLevel extends Level implements KeyListener {
	
	public static class MenuItem {
		MenuLevel menu;
		String text;
		Runnable action;
		boolean mouseDown = false;
		Rectangle mouser = new Rectangle();
		public MenuItem(MenuLevel menu, String text, Runnable action) {
			this.menu = menu;
			this.text = text;
			this.action = action;
		}
	}
	
	Array<MenuItem> items = new Array<MenuItem>();
	MenuLevel parent;
	MenuItem current;
	Level bglevel;
	boolean bgpaused = true;
	Color dimm = new Color(0f, 0f, 0f, 0.2f);
	float stepspeed = 0.9f;
	float step = 0f;
	int logostep = 0;
	boolean showtitle = true;
	
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
		
		System.gc();
	}
	
	static Rectangle r = new Rectangle();
	
	@Override
	public void tick() {
		if (bglevel != null) {
			boolean lastInteract = true;
			if (bglevel.player != null) {
				player = bglevel.player;
				lastInteract = player.canInteract;
				player.canInteract = false;
			}
			if (!bgpaused) {
				bglevel.tick();
			} else {
				//bglevel.c.tick();
				//for (Cursor c : bglevel.cursors) {
				//	c.tick();
				//}
			}
			if (bglevel.player != null) {
				player.canInteract = lastInteract;
			}
		} else {
			player = null;
		}
		if (current == null || !items.contains(current, true)) {
			current = items.get(0);
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
			
			Rectangle vp = Shadow.cam.camrec;
			
			float mx = newpos.x;
			float my = newpos.y;
			
			Rectangle	r = new Rectangle();
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
				} else {
				}
			}
			
		}
		
		step *= stepspeed;
		logostep++;
	}
	
	Vector2 oldpos;
	
	public Vector2 calcMousePos(Vector2 apos) {
		if (oldpos == null) {
			oldpos = new Vector2();
		}
		oldpos.set(apos);
		Vector2 pos = Garbage.vec2;
		pos.set(apos);
		float tx = 0;
		float ty = 0;
		float cx = Shadow.cam.camrec.x;
		float cy = Shadow.cam.camrec.y;
		float mx = (pos.x * (Shadow.vieww/Shadow.dispw)) * Shadow.cam.cam.zoom;
		float my = (pos.y * (Shadow.viewh/Shadow.disph)) * Shadow.cam.cam.zoom;
		tx = mx + cx;
		ty = my + cy;
		float otx = tx;
		float oty = ty;
		pos.set(tx, ty);
		return pos;
	}

	Image logo;
	Image image;
	Image dimmimg;
	BitmapFont font;
	
	boolean omitloop = false;
	
	@Override
	public void renderImpl() {
		if (bglevel != null && !omitloop) {
			omitloop = true;
			bglevel.canRenderImpl = false;
			Shadow.cam.renderLevel(bglevel);
			bglevel.canRenderImpl = true;
			//Shadow.cam.renderLevel(this);
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
		
		if (font == null) {
			if (Shadow.isAndroid) {
				font = Fonts.light_large;
			} else {
				font = Fonts.light_normal;
			}
		}
		font.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);
		
		float x1 = font.getScaleX();
		float y1 = font.getScaleY();
		
		if (showtitle) {
			if (logo == null) {
				logo = Images.getImage("logo");
			}
			logo.setScale(font.getScaleX(), font.getScaleY());
			float possibruu = (float)Math.sin(logostep/16f)/8f;
			logo.setPosition(vp.x + vp.width - logo.getWidth()*logo.getScaleX() - x1*32f - 0.125f, vp.y - logo.getHeight()*logo.getScaleY() - y1*32f + (float)Math.sin(logostep/32f)/8f + possibruu - 0.125f);
			logo.draw(Shadow.spriteBatch, 1f);
		}
		
		float maxw = 0;
		for (MenuItem mi : items) {
			String txt = mi.text;
			TextBounds tb = font.getBounds(txt);
			if (tb.width > maxw) {
				maxw = tb.width;
			}
		}
		float texth = font.getLineHeight();
		int i = 0;
		for (MenuItem mi : items) {
			String txt = mi.text;
			TextBounds tb = font.getBounds(txt);
			
			float x = vp.width/2 - maxw;
			x += vp.x + vp.width/2;
			x -= x1*32f;
			float y = vp.height/2 - (items.size*(-texth) + i*texth);
			y += vp.y + vp.height/2;
			y += y1*48f;
			
			font.setColor(0f, 0f, 0f, 0.5f);
			font.draw(Shadow.spriteBatch, txt, x + 0.0825f, y + 0.0825f);
			font.setColor(1f, 1f, 1f, 1f);
			font.draw(Shadow.spriteBatch, txt, x, y);
			
			mi.mouser.set(x, y, tb.width, tb.height);
			
			if (mi == current) {
				if (image == null) {
					TextureRegion[][] regs = Images.split("player", 16, 16);
					TextureRegion reg = null;
					reg = regs[1][0];
					image = new Image(reg);
				}
				
				float zoomscale = 2f;
				
				if (Shadow.isAndroid) {
					zoomscale = 4f;
				}
				
				image.setScale(font.getScaleX()*zoomscale, font.getScaleY()*zoomscale);
				image.setPosition(x - 16f*image.getScaleX()*1.5f + 0.0825f, (y+step) - 16f*image.getScaleY() - 3f/16f + 0.0825f);
				image.setColor(0f, 0f, 0f, 0.5f);
				image.draw(Shadow.spriteBatch, 1f);
				image.setPosition(image.getX() - 0.0825f, image.getY() - 0.0825f);
				image.setColor(1f, 1f, 1f, 1f);
				image.draw(Shadow.spriteBatch, 1f);
			}
			
			i++;
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
			//TODO: Decide if wrap or not.
			if (index < 0) {
				index = 0;
				//index = items.size()-1;
			}
			if (index >= items.size) {
				index = items.size-1;
				//index = 0;
			}
			current = items.get(index);
			step += (oindex - index)*0.75f;
		}
		if (key == Input.enter) {
			if (current == null) {
				current = items.get(0);
			}
			if (current != null) {
				Runnable action = current.action;
				if (action != null) {
					action.run();
				}
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
