package net.fourbytes.shadow.utils.backend.opengl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

public class GLUtil {

	public byte[] getScreenData(int x, int y, int w, int h) {
		//Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);
		ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 3);
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGB, GL10.GL_UNSIGNED_BYTE, pixels);
		int numBytes = w * h * 3;
		byte[] data = new byte[numBytes];
		pixels.clear();
		pixels.get(data);
		return data;
	}

}
