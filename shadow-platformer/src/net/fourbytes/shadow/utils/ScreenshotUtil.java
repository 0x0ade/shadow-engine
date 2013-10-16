package net.fourbytes.shadow.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.utils.backend.BackendHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class ScreenshotUtil {
	private ScreenshotUtil() {
	}

	private static Pixmap[] pixmaps = new Pixmap[2];
	private static int index = 0;

	private static Pixmap getPixmap() {
		int w = (int)Shadow.dispw;
		int h = (int)Shadow.disph;
		Pixmap pixmap = pixmaps[index];
		if (pixmap == null || (pixmap.getWidth() != w || pixmap.getHeight() != h)) {
			if (pixmap != null) {
				pixmap.dispose();
			}
			pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
			pixmaps[index] = pixmap;
		}
		index++;
		if (index >= pixmaps.length) {
			index = 0;
		}
		return pixmap;
	}

	public static void fillPixmap(Pixmap pixmap, byte[] data) {
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();
		int x = 0;
		int y = h-1;
		for (int i = 0; i < data.length; i += 3) {
			byte r = data[i+0];
			byte g = data[i+1];
			byte b = data[i+2];

			pixmap.setColor((r & 0xFF) / 255f, (g & 0xFF) / 255f, (b & 0xFF) / 255f, 1f);
			pixmap.drawPixel(x, y);

			x++;
			if (x >= w) {
				y--;
				x = 0;
			}
		}
	}

	public static void saveScreen() {
		final int w = (int)Shadow.dispw;
		final int h = (int)Shadow.disph;

		final byte[] data = BackendHelper.glUtil.getScreenData(0, 0, w, h);

		Thread thread = new Thread("Screenshot_"+Shadow.rand.nextInt(1024)) {
			@Override
			public void run() {
				Pixmap pixmap = getPixmap();
				fillPixmap(pixmap, data);

				FileHandle fh = Shadow.getDir("screenshots").child("screen_"+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+".png");
				fh.parent().mkdirs();

				PixmapIO.writePNG(fh, pixmap);
			}
		};
		thread.start();
	}

	public static int tick = 0;
	public static int speed = 2;
	public static int frame = 0;

	public static void pushFrame() {
		if (tick < speed-1) {
			tick++;
			return;
		}
		tick = 0;
		frame++;

		final int w = (int)Shadow.dispw;
		final int h = (int)Shadow.disph;

		final byte[] data = BackendHelper.glUtil.getScreenData(0, 0, w, h);

		Thread thread = new Thread("Screenshot_"+Shadow.rand.nextInt(1024)) {
			@Override
			public void run() {
				Pixmap pixmap = getPixmap();
				fillPixmap(pixmap, data);

				FileHandle fh = Shadow.getDir("gifs").child(Shadow.recordDirName).child("screen_"+frame+".png");
				fh.parent().mkdirs();

				PixmapIO.writePNG(fh, pixmap);
			}
		};
		thread.start();
	}

	public static void pullRecord() {
		frame = 0;
		//TODO Bind all PNGs into one GIF
	}
}
