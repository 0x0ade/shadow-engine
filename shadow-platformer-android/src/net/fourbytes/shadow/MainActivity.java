package net.fourbytes.shadow;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.*;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.GdxRuntimeException;
import net.fourbytes.shadow.utils.backend.AndroidBackend;
import net.fourbytes.shadow.utils.backend.BackendHelper;
import net.fourbytes.shadow.utils.backend.OuyaBackend;
import net.fourbytes.slimodk.SlimODK;

import java.lang.reflect.Method;

public class MainActivity extends AndroidApplication {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        */

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGLSurfaceView20API18 = Build.VERSION.SDK_INT >= 18;
        //cfg.useGLSurfaceView20API18 = false;

        Input.isAndroid = true;
        Shadow.isAndroid = true;
		//TODO integrate the dev UUID somewhere else...
		//TODO use a sample dev UUID
		SlimODK.init(this, "932e4746-a42b-49a3-8aad-d1af9ce7ecc2");
		if (SlimODK.getDeviceID() > -2) {
			Input.isOuya = true;
			Shadow.isOuya = true;

			BackendHelper.backend = new OuyaBackend(cfg);
		} else {
			SlimODK.end();

			BackendHelper.backend = new AndroidBackend(cfg);
		}

        initialize(new Shadow(), cfg);
    }

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config) {
		if (this.getVersion() < MINIMUM_SDK) {
			throw new GdxRuntimeException("LibGDX requires Android API Level " + MINIMUM_SDK + " or later.");
		}

		//Reordered to make using Gdx.app, Gdx.graphics etc possible (due to AndroidBackend.getGdxInput)

		graphics = new AndroidGraphics(this, config,
				config.resolutionStrategy == null ? new FillResolutionStrategy() : config.resolutionStrategy);
		audio = new AndroidAudio(this, config);
		this.getFilesDir(); // workaround for Android bug #10515463
		files = new AndroidFiles(this.getAssets(), this.getFilesDir().getAbsolutePath());
		net = new AndroidNet(this);
		this.listener = listener;
		this.handler = new Handler();
		this.useImmersiveMode = config.useImmersiveMode;
		this.hideStatusBar = config.hideStatusBar;

		// Add a specialized audio lifecycle listener
		addLifecycleListener(new LifecycleListener() {

			@Override
			public void resume () {
				// No need to resume audio here
			}

			@Override
			public void pause () {
				//audio.pause(); //screw it, world shall explode as I can't change it...
			}

			@Override
			public void dispose () {
				audio.dispose();
			}
		});

		Gdx.app = this;
		Gdx.audio = this.getAudio();
		Gdx.files = this.getFiles();
		Gdx.graphics = this.getGraphics();
		Gdx.net = this.getNet();

		input = ((AndroidBackend)BackendHelper.backend).getGdxInput();
		if (input == null) {
			input = AndroidInputFactory.newAndroidInput(this, this, graphics.getView(), config);
		}

		Gdx.input = this.getInput();

		try {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		} catch (Exception ex) {
			log("AndroidApplication", "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(graphics.getView(), createLayoutParams());

		createWakeLock(config.useWakelock);
		hideStatusBar(this.hideStatusBar);
		useImmersiveMode(this.useImmersiveMode);
		if (this.useImmersiveMode && getVersion() >= 19) {
			try {
				Class<?> vlistener = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
				Object o = vlistener.newInstance();
				Method method = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
				method.invoke(o, this);
			} catch (Exception e) {
				log("AndroidApplication", "Failed to create AndroidVisibilityListener", e);
			}
		}
	}
}