package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

public class TimeDaySystem {

	public Level level;
	//delta in seconds!
	public float fullday = 60f * 10f;
	//public float fullday = 14f;
	public float day = 0;
	//public float delta = fullday/2f - fullday/14f;
	public float delta = 0f;
	
	public static Array<Color> colors = new Array<Color>();
	public static Array<Color> colorsbg = new Array<Color>();
	static {
		colors.add(new Color(0.125f, 0.25f, 0.5f, 1f));
		colors.add(new Color(0.125f, 0.25f, 0.5f, 1f));
		colors.add(new Color(0.125f, 0.25f, 0.5f, 1f));
		colors.add(new Color(0.7f, 0.6f, 0.5f, 1f));
		colors.add(new Color(1f, 1f, 1f, 1f));
		colors.add(new Color(1f, 1f, 1f, 1f));
		colors.add(new Color(0.7f, 0.6f, 0.5f, 1f));
		
		colorsbg.add(new Color(0f, 0.125f, 0.3f, 1f));
		colorsbg.add(new Color(0f, 0.125f, 0.3f, 1f));
		colorsbg.add(new Color(0f, 0.125f, 0.3f, 1f));
		colorsbg.add(new Color(0.7f, 0.6f, 0.3f, 1f)); 
		colorsbg.add(new Color(0.2f, 0.5f, 0.7f, 1f));
		colorsbg.add(new Color(0.2f, 0.5f, 0.7f, 1f));
		colorsbg.add(new Color(0.7f, 0.6f, 0.3f, 1f));
	}
	
	public TimeDaySystem(Level level) {
		this.level = level;
	}
	
	protected Color tmp = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg2 = new Color(1f, 1f, 1f, 1f);
	protected Color tmpc = new Color(1f, 1f, 1f, 1f);
	
	public void tick() {
		if (Shadow.isAndroid) {
			//return; //FIXME PERFORMANCE
		}
		int ci = (int)((delta/fullday)*colors.size)%colors.size;
		float f = ((delta%(fullday/colors.size))/(fullday/colors.size))%1f;
		int cii = ci + 1;
		if (cii >= colors.size) {
			cii = 0;
		}
		int cci = (int)(((delta+fullday/16f)/fullday)*colors.size)%colors.size;
		float ff = (((delta+fullday/16f)%(fullday/colors.size))/(fullday/colors.size))%1f;
		int ccii = cci + 1;
		if (ccii >= colors.size) {
			ccii = 0;
		}
		
		tmp.set(tmpc.set(colors.get(ci)).mul(1f-f));
		tmp.add(tmpc.set(colors.get(cii)).mul(f));
		
		tmpbg.set(tmpc.set(colorsbg.get(ci)).mul(1f-f));
		tmpbg.add(tmpc.set(colorsbg.get(cii)).mul(f));
		tmpbg2.set(tmpc.set(colorsbg.get(cci)).mul(1f-ff));
		tmpbg2.add(tmpc.set(colorsbg.get(ccii)).mul(ff));
		
		level.globalLight.set(tmp);
		Shadow.cam.bg.c1.set(tmpbg);
		Shadow.cam.bg.c2.set(tmpbg2).sub(0.2f, 0.2f, 0.2f, 0f);
		if ((ci >= 0 || cii >= 0) && (ci <= 2 || cii <= 2)) {
				if (cii == 0) {
					Shadow.cam.bg.starsAlpha = (delta-3f*(fullday/4f))/(fullday/4f);
				} else {
					Shadow.cam.bg.starsAlpha = 1f-delta/(fullday/4f);
				}
		} else {
			Shadow.cam.bg.starsAlpha = 0f;
		}
		Shadow.cam.bg.starsScrollX = 0f;
		Shadow.cam.bg.starsScrollY = 0f;
		float adddelta = Gdx.graphics.getDeltaTime();
		Shadow.cam.bg.sunPos += adddelta;
		Shadow.cam.bg.sunRound = fullday;
		delta += adddelta;
		if (delta > fullday) {
			delta -= fullday;
			day++;
		}
	}

}
