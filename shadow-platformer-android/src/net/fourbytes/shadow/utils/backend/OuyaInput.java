package net.fourbytes.shadow.utils.backend;

import android.content.Context;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidInputThreePlus;
import com.badlogic.gdx.graphics.Pixmap;
import net.fourbytes.slimodk.SlimODKCursor;

/**
 * This class extends {@link com.badlogic.gdx.backends.android.AndroidInputThreePlus} and
 * overrides the cursor image setting method as Ouya actually supports (partial) cursor setup.
 */
public class OuyaInput extends AndroidInputThreePlus {

	public OuyaInput(Application activity, Context context, Object view, AndroidApplicationConfiguration config) {
		super(activity, context, view, config);
	}

	public void setCursorVisible(boolean showCursor) {
		SlimODKCursor.setCursorVisible(showCursor);
	}

	@Override
	public void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
		SlimODKCursor.setCursorImage(pixmap, xHotspot, yHotspot);
	}

}
