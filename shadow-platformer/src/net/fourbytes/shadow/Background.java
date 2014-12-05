package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.utils.Options;

public class Background {
	
	public static class Star {
		public Vector2 pos = new Vector2();
		public Vector2 spp = new Vector2();
		public Vector2 vpp = new Vector2();
		public float scale = 1f;
		public float depth = 0f;
		public Color color;
		public float alpha = 0;
		public float dur = 1;
		public float tick = 0;
		
		public Star() {
			color = new Color(1f, 1f, 1f, 0f);
			float ff = 5f;
			color.sub(MathUtils.random(1f/ff), MathUtils.random(1f/ff), MathUtils.random(1f/ff), 0f);
		}
		
		public void tick() {
			tick++;
			if (tick < dur/2f) {
				alpha = tick/dur;
			} else {
				alpha = 1f-tick/dur;
			}
			if (tick >= dur) {
				tick = 0;
				dur = 100f+MathUtils.random(100f);
				alpha = 0f;
				vpp.x = vp.x;
				vpp.y = vp.y;
				spp.x = MathUtils.random(vp.width);
				spp.y = MathUtils.random(vp.height);
				pos.x = vpp.x+spp.x;
				pos.y = vpp.y+spp.y;
				scale = MathUtils.random(1f/16f)+(1f/8f);
				depth = 1.825f+MathUtils.random(1f/2f);
				color.set(1f, 1f, 1f, 0f);
				float ff = 5f;
				color.sub(MathUtils.random(1f/ff), MathUtils.random(1f/ff), MathUtils.random(1f/ff), 0f);
			}
		}
	}
	
	public Color c1;
	public Color c2;
	
	public Array<Star> stars = new Array<Star>(false, 32, Star.class);
	{
		for (int i = 0; i < 25; i++) {
			stars.add(new Star());
		}
	}
	public float starsAlpha = 0f;
	public float starsScrollX = 0f;
	public float starsScrollY = 0f;
	public float sunPos = 0f;
	public float sunRound = 1f;
	
	protected Background(Color c1, Color c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public static Image white;
	
	final static Color cc1 = new Color();
	final static Color cc2 = new Color();
	final static Rectangle vp = new Rectangle();

	public void render() {
		//Shadow.spriteBatch.disableBlending();//TODO Decide / Benchmark / Test / ...
		if (white == null) {
			white = Images.getImage("white");
		}
		float barh = 2f;
		
		vp.set(Shadow.cam.camrec);
		
		white.setSize(1f, -1f);
		float scaleX = Shadow.vieww * Shadow.cam.cam.zoom;
		float scaleY = barh * Shadow.cam.cam.zoom;
		white.setScale(scaleX, scaleY);
		white.setColor(c1);
		
		float i = 0;
		float imax = vp.height / -(barh / Shadow.cam.cam.zoom) - (2f*barh / Shadow.cam.cam.zoom);

		for (float y = vp.y + vp.height; y > vp.y; y -= scaleY) {
			white.setPosition(vp.x, y);
			
			cc1.set(c1);
			cc2.set(c2);
			
			cc1.mul(i/imax);
			cc2.mul(1f - i/imax);
			white.setColor(cc1);
			white.getColor().add(cc2).a = 1f;

			white.draw(Shadow.spriteBatch, 1f);
			i -= scaleY;
		}
		
		Shadow.spriteBatch.enableBlending();
		
		if (starsAlpha > 0f) {
            for (int ii = 0; ii < stars.size; ii++) {
				Star s = stars.items[ii];
				s.pos.add(starsScrollX, starsScrollY);
				s.vpp.add(starsScrollX, starsScrollY);
				if (!vp.contains(s.pos.x, s.pos.y)) {
					s.tick = s.dur;
					s.tick();
				}
				s.tick();
				s.color.a = starsAlpha*s.alpha;
				white.setColor(s.color);
				white.setPosition(s.vpp.x-((s.vpp.x-vp.x)/s.depth)+s.spp.x, s.vpp.y-((s.vpp.y-vp.y)/s.depth)+s.spp.y);
				white.setSize(1f, -1f);
				white.setScale(s.scale);
				white.draw(Shadow.spriteBatch, 1f);
			}
		} else {
			for (int ii = 0; ii < stars.size; ii++) {
				Star s = stars.items[ii];
				s.tick();
			}
		}
	}

	public static Background getShade(Color c1, Color c2) {
		return new Background(c1, c2);
	}
	
	public static Background getShade(Color c1) {
		Color c2 = new Color(c1);
		c2.sub(0.2f, 0.2f, 0.2f, 0f);
		return getShade(c1, c2);
	}

	public static Background getDefault() {
		/*
		Background bg = Background.getShade(new Color(0f, 0.125f, 0.3f, 1f));
		bg.starsAlpha = 1f;
		bg.starsScrollY = 0.01f;
		*/
		return Background.getShade(new Color(0.2f, 0.5f, 0.7f, 1f), new Color(0f, 0.125f, 0.3f, 1f));
	}

}
