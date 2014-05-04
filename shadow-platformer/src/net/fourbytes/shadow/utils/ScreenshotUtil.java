package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import net.fourbytes.shadow.Shadow;

import java.nio.ByteBuffer;

public final class ScreenshotUtil {
	private ScreenshotUtil() {
	}

	private static byte[][] datas = new byte[32][];
	private static int indexData = 0;

	private static byte[] getData(int length) {
		byte[] data = datas[indexData];
		if (data == null || data.length != length) {
			data = new byte[length];
			datas[indexData] = data;
		}
		indexData++;
		if (indexData >= datas.length) {
			indexData = 0;
		}
		return data;
	}

	private static ByteBuffer[] pixelBuffers = new ByteBuffer[32];
	private static int indexPixelBuffer = 0;

	private static ByteBuffer getPixelBuffer(int capacity) {
		ByteBuffer pixels = pixelBuffers[indexPixelBuffer];
		if (pixels == null || pixels.capacity() != capacity) {
			pixels = BufferUtils.newByteBuffer(capacity);
			pixelBuffers[indexPixelBuffer] = pixels;
		}
		indexPixelBuffer++;
		if (indexPixelBuffer >= pixelBuffers.length) {
			indexPixelBuffer = 0;
		}
		return pixels;
	}

	public static byte[] getScreenData(int x, int y, int w, int h) {
		//Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
		int numBytes = w * h * 3;
		ByteBuffer pixels = getPixelBuffer(numBytes);
		Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);
		byte[] data = getData(numBytes);
		pixels.clear();
		pixels.get(data);
		return data;
	}

	private static Pixmap[] pixmaps = new Pixmap[32];
	private static int indexPixmap = 0;

	private static Pixmap getPixmap(int w, int h) {
		Pixmap pixmap = pixmaps[indexPixmap];
		if (pixmap == null || (pixmap.getWidth() != w || pixmap.getHeight() != h)) {
			if (pixmap != null) {
				pixmap.dispose();
			}
			pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
			pixmaps[indexPixmap] = pixmap;
		}
		indexPixmap++;
		if (indexPixmap >= pixmaps.length) {
			indexPixmap = 0;
		}
		return pixmap;
	}

	public static void fillPixmap(Pixmap pixmap, byte[] data) {
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();
		int x = 0;
		int y = h-1;
		for (int i = 0; i < data.length; i += 3) {
			byte r = data[i];
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

	public static void frameSave() {
		frameSave(Shadow.getDir("screenshots").child("screen_"+Garbage.dateCurrent()+".png"));
	}

	public static void frameSave(final FileHandle fh) {
		final int w = (int) Shadow.dispw;
		final int h = (int) Shadow.disph;

		final byte[] data = getScreenData(0, 0, w, h);

		Thread thread = new Thread("Screenshot_"+Shadow.rand.nextInt(1024)) {
			@Override
			public void run() {
				Pixmap pixmap = getPixmap(w, h);
				fillPixmap(pixmap, data);

				fh.parent().mkdirs();

				PixmapIO.writePNG(fh, pixmap);
			}
		};
		thread.start();
	}

	public static int tick = 0;
	public static int speed = 2;
	public static int frame = 0;

	public static void frameRecord() {
		if (tick < speed-1) {
			tick++;
			return;
		}
		tick = 0;
		frame++;

		frameSave(Shadow.getDir("gifs").child(Shadow.recordDirName).child("screen_"+frame+".png"));
	}

	public static void framesPull() {
		frame = 0;
		//TODO Bind all PNGs into one GIF
	}

}
