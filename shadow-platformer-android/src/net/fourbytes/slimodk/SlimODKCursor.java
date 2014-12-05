package net.fourbytes.slimodk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Contains alternative versions to the ODK's cursor methods.
 * <br>
 * Part of SlimODK but doesn't require other parts to be initialized.
 */
public final class SlimODKCursor {
	private SlimODKCursor() {
	}

	public static void setCursorVisible(boolean showCursor) {
		String ACTION_SHOW_CURSOR = "tv.ouya.controller.action.SHOW_CURSOR";
		String ACTION_HIDE_CURSOR = "tv.ouya.controller.action.HIDE_CURSOR";
		Intent intent = new Intent(showCursor ? ACTION_SHOW_CURSOR : ACTION_HIDE_CURSOR);
		((Activity) Gdx.app).getApplicationContext().sendBroadcast(intent);
	}

	public static void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
		if (pixmap == null) {
			setCursorVisible(false);
			return;
		}
		setCursorVisible(true);

		System.out.println("Setting cursor");

		Bitmap bitmap = convertPixmapToBitmap(pixmap);

		Intent intent = new Intent("tv.ouya.controller.action.SET_CURSOR_BITMAP");
		intent.putExtra("CURSOR_BITMAP", bitmap);
		//TODO modify hotspot if needed
		intent.putExtra("HOTSPOT_X", (float) xHotspot / (float) pixmap.getWidth());
		intent.putExtra("HOTSPOT_Y", (float)yHotspot/(float)pixmap.getHeight());
		((Activity) Gdx.app).getApplicationContext().sendBroadcast(intent);
		System.out.println("Intent sent");
	}

	private static Bitmap convertPixmapToBitmap(Pixmap pixmap) {
		Pixmap.Format pixmapFormat = pixmap.getFormat();

		Bitmap bitmap = Bitmap.createBitmap(pixmap.getWidth(), pixmap.getHeight(),
				getConfigForFormat(pixmapFormat));

		ByteBuffer src = pixmap.getPixels();
		ByteBuffer dst = src;

		if (pixmapFormat == Pixmap.Format.RGBA4444 || pixmapFormat == Pixmap.Format.RGBA8888) {
			int capacity = pixmap.getWidth()*pixmap.getHeight()*4;
			dst = BufferUtils.newByteBuffer(capacity);

			byte[] orig = new byte[4];
			byte[] modif = new byte[4];
			for (int i = 0; i < capacity; i += 4) {
				src.get(orig);
				System.arraycopy(orig, 0, modif, 1, 3);
				modif[0] = orig[3];
				dst.put(modif);
			}

		} else if (pixmapFormat == Pixmap.Format.RGB888) {
			int capacity = pixmap.getWidth()*pixmap.getHeight()*4;
			dst = BufferUtils.newByteBuffer(capacity);

			byte[] orig = new byte[3];
			byte[] modif = new byte[4];
			for (int i = 0; i < capacity; i += 4) {
				src.get(orig);
				System.arraycopy(orig, 0, modif, 1, 3);
				modif[0] = Byte.MAX_VALUE;
				dst.put(modif);
			}
		}

		dst.position(0);

		bitmap.copyPixelsFromBuffer(dst);

		return bitmap;
	}

	private static Bitmap.Config getConfigForFormat(Pixmap.Format format) {
		switch (format) {
			case Alpha:
				return Bitmap.Config.ALPHA_8;
			case Intensity:
				return Bitmap.Config.ALPHA_8;
			case LuminanceAlpha:
				return Bitmap.Config.ALPHA_8;
			case RGB565:
				return Bitmap.Config.RGB_565;
			case RGBA4444:
				return Bitmap.Config.ARGB_4444;
			case RGB888:
				return Bitmap.Config.ARGB_8888;
			case RGBA8888:
				return Bitmap.Config.ARGB_8888;
		}
		return null;
	}

}
