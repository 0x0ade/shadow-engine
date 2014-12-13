package net.fourbytes.shadow.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Shadow;

public class DefaultTimeDaySystem implements ITimeDaySystem {

	public Level level;
	//delta in seconds!
	public float fullday = 60f * 5f;
	//public float fullday = 14f;
	public float day = -1;
	public float time = fullday/2f - fullday/14f;
	//public float time = 0f;

	public static Color[] colors = new Color[] {
			new Color(0.125f, 0.25f, 0.5f, 1f),
			new Color(0.125f, 0.25f, 0.5f, 1f),
			new Color(0.125f, 0.25f, 0.5f, 1f),
			new Color(0.9f, 0.85f, 0.8f, 1f),
			new Color(1f, 1f, 1f, 1f),
			new Color(1f, 1f, 1f, 1f),
			new Color(0.9f, 0.85f, 0.8f, 1f)
	};
	public static Color[] colorsbg = new Color[] {
			new Color(0f, 0.125f, 0.3f, 1f),
			new Color(0f, 0.125f, 0.3f, 1f),
			new Color(0f, 0.125f, 0.3f, 1f),
			new Color(0.7f, 0.6f, 0.3f, 1f),
			new Color(0.2f, 0.5f, 0.7f, 1f),
			new Color(0.2f, 0.5f, 0.7f, 1f),
			new Color(0.7f, 0.6f, 0.3f, 1f)
	};

	public DefaultTimeDaySystem(Level level) {
		this.level = level;
	}
	
	protected Color tmp = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg = new Color(1f, 1f, 1f, 1f);
	protected Color tmpbg2 = new Color(1f, 1f, 1f, 1f);

	@Override
	public void tick(float delta) {
		int numColors = colors.length;
		float offs = fullday/16f;

		float fUp = (time % (fullday / numColors)) / (fullday/numColors) % 1f;
		float fDown = ((( time + offs) % (fullday / numColors)) / (fullday / numColors)) % 1f;

		int iUp = (int) ((time / fullday) * numColors) % numColors;
		int iUpNext = (iUp + 1) % numColors;

		int iDown = (int) (((time + offs) / fullday) * numColors) % numColors;
		int iDownNext = (iDown + 1) % numColors;

		//getting colors

		Color cUp = colors[iUp];
		Color cUpNext = colors[iUpNext];

		Color bgUp = colorsbg[iUp];
		Color bgUpNext = colorsbg[iUpNext];
		Color bgDown = colorsbg[iDown];
		Color bgDownNext = colorsbg[iDownNext];

		//protip: avoid method calls such as set, mul, sub, etc to pixelpush performance

		//setting light color

		tmp.r = cUp.r * (1f - fUp) + cUpNext.r * fUp;
		tmp.g = cUp.g * (1f - fUp) + cUpNext.g * fUp;
		tmp.b = cUp.b * (1f - fUp) + cUpNext.b * fUp;
		tmp.a = cUp.a * (1f - fUp) + cUpNext.a * fUp;

		//setting bg up color

		tmpbg.r = bgUp.r * (1f - fUp) + bgUpNext.r * fUp;
		tmpbg.g = bgUp.g * (1f - fUp) + bgUpNext.g * fUp;
		tmpbg.b = bgUp.b * (1f - fUp) + bgUpNext.b * fUp;
		tmpbg.a = bgUp.a * (1f - fUp) + bgUpNext.a * fUp;

		//setting bg down color

		tmpbg2.r = bgDown.r * (1f - fDown) + bgDownNext.r * fDown - 0.2f;
		tmpbg2.g = bgDown.g * (1f - fDown) + bgDownNext.g * fDown - 0.2f;
		tmpbg2.b = bgDown.b * (1f - fDown) + bgDownNext.b * fDown - 0.2f;
		tmpbg2.a = bgDown.a * (1f - fDown) + bgDownNext.a * fDown - 0.2f;

		//setting values in level and cam.bg

		level.globalLight.r = tmp.r;
		level.globalLight.g = tmp.g;
		level.globalLight.b = tmp.b;
		level.globalLight.a = tmp.a;

		Shadow.cam.bg.c1.r = tmpbg.r;
		Shadow.cam.bg.c1.g = tmpbg.g;
		Shadow.cam.bg.c1.b = tmpbg.b;
		Shadow.cam.bg.c1.a = tmpbg.a;

		Shadow.cam.bg.c2.r = tmpbg2.r;
		Shadow.cam.bg.c2.g = tmpbg2.g;
		Shadow.cam.bg.c2.b = tmpbg2.b;
		Shadow.cam.bg.c2.a = tmpbg2.a;

		Shadow.cam.bg.starsAlpha = MathUtils.cosDeg((time / fullday) * 360f - 45f);

		float adddelta = 0f;
		if (day < 0) {
			day++;
			nextDay();
		} else if (!level.paused) {
			adddelta = delta;
		}

		Shadow.cam.bg.sunPos = time;
		Shadow.cam.bg.sunRound = fullday;
        time += adddelta;
		if (delta > fullday) {
            time -= fullday;
			day++;
			nextDay();
		}
	}

	public void nextDay() {
		INextDay[] isystems = level.systems.getAll(INextDay.class);
		for (int i = 0; i < isystems.length; i++) {
			INextDay isystem = isystems[i];
			isystem.nextDay();
		}
	}

    @Override
    public float getTime() {
        return time;
    }

    @Override
    public int getDay() {
        return (int) day;
    }

    @Override
    public float getDayDuration() {
        return fullday;
    }

    @Override
    public float getTimeNormalized() {
        return time / fullday;
    }

    @Override
    public void setTime(float time) {
        if (time > fullday) {
            time -= fullday;
            day++;
        } else if (time < 0f) {
            time += fullday;
            day--;
        }
        this.time = time;
    }

    @Override
    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public void setDayDuration(float fullday) {
        this.fullday = fullday;
    }

    @Override
    public String getName() {
        return "TimeDaySystem";
    }

}
