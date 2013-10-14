package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import net.fourbytes.shadow.Shadow;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class ScreenshotUtil {
	private ScreenshotUtil() {
	}

	public static byte[] getScreenData(int x, int y, int w, int h) {
		//Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);
		ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 3);
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGB, GL10.GL_UNSIGNED_BYTE, pixels);
		int numBytes = w * h * 3;
		byte[] data = new byte[numBytes];
		pixels.clear();
		pixels.get(data);
		return data;
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

		final byte[] data = getScreenData(0, 0, w, h);

		Thread thread = new Thread("Screenshot_"+Shadow.rand.nextInt(1024)) {
			@Override
			public void run() {
				Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
				fillPixmap(pixmap, data);

				FileHandle fh = Shadow.getDir("screenshots").child("screen_"+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+".png");
				fh.parent().mkdirs();

				PixmapIO.writePNG(fh, pixmap);
				pixmap.dispose();
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

		final int w = (int)Shadow.dispw;
		final int h = (int)Shadow.disph;

		final byte[] data = getScreenData(0, 0, w, h);

		Thread thread = new Thread("Screenshot_"+Shadow.rand.nextInt(1024)) {
			@Override
			public void run() {
				Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
				fillPixmap(pixmap, data);

				FileHandle fh = Shadow.getDir("gifs").child(Shadow.recordDirName).child("screen_"+frame+".png");
				fh.parent().mkdirs();

				PixmapIO.writePNG(fh, pixmap);
				pixmap.dispose();

				frame++;
			}
		};
		thread.start();
	}

	public static void pullRecord() {
		frame = 0;
		//TODO Bind all PNGs into one GIF
	}
}
