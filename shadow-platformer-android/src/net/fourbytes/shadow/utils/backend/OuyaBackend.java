package net.fourbytes.shadow.utils.backend;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.backends.android.AndroidInput;

public class OuyaBackend extends AndroidBackend {
	public OuyaBackend(AndroidApplicationConfiguration cfgApp) {
		super(cfgApp);
	}

	@Override
	public void create() {
	}

	@Override
	public ModLoader newModLoader() {
		return new AndroidModLoader();
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new OuyaControllerNumerator();
	}

	@Override
	public AndroidInput getGdxInput() {
		return new OuyaInput(Gdx.app, ((Activity)Gdx.app), ((AndroidGraphics)Gdx.graphics).getView(), cfgApp);
	}

}
