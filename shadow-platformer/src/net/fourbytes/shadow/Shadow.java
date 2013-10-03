package net.fourbytes.shadow;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import net.fourbytes.shadow.Input.Key;
import net.fourbytes.shadow.Input.Key.Triggerer;
import net.fourbytes.shadow.Input.KeyListener;
import net.fourbytes.shadow.Input.TouchPoint;
import net.fourbytes.shadow.Input.TouchPoint.TouchMode;
import net.fourbytes.shadow.mod.ModLoader;
import net.fourbytes.shadow.network.NetClient;
import net.fourbytes.shadow.network.NetServer;
import net.fourbytes.shadow.network.NetStream;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public final class Shadow implements ApplicationListener, InputProcessor, KeyListener {
	
	public static Random rand = new Random();
	
	public static Level level;
	public static ControllerHelper controllerHelper;
	
	public static Camera cam;
	public static float dispw = 1f;
	public static float disph = 1f;
	/**
	 * 0x00 = Fully dynamic (Useless, ugly, ...) <br>
	 * 0x01 = Fixed height (Mobile devices, small screens) <br>
	 * 0x02 = Fixed width (PC and Ouya, larger resolutions) <br>
	 * 0x03 = Fully fixed (Resizing doesn't scale) <br>
	 * 0x04 = Automatic scaling (Does what it says) <br>
	 * Other: Gliatch.
	 */
	//TODO move magic numbers to class or enum
 	public static byte viewmode = 0x00;
	/**
	 * View Fixed Factor
	 */
	public static float viewff = 32f;
	public static float vieww = 1f;
	public static float viewh = 1f;
	public static float touchw = 1f;
	public static float touchh = 1f;
	public static ShaderProgram shader;
	public static SpriteBatch spriteBatch;
	
	public static int frames = 0;
	public static long lastfmicro = 0;
	public static int fps = 0;
	public static int eframes = 0;
	public static long elastfmicro = 0;
	public static int efps = 0;
	
	public static boolean isAndroid = false;
	public static boolean isOuya = false;
	public static boolean gdxpaused = false;
	
	public static int loadstate = 0;
	public static int loadtick = 0;
	public static int[][] loadticks = {{0, 1, 2, 3, 4, 5, 6}};
	
	public static String clientID = getSecureRandomBytes(512);
	public static NetStream client;
	public static NetStream server;
	
	public Shadow() {
		super();
	}
	
	/**
	 * Creates an string containing an given amount of random hexadecimal bytes. <br> 
	 * It's using SecureRandom instead of Java's default Random.
	 * @param amount Amount of random bytes
	 * @return Random client identifier.
	 */
	public static String getSecureRandomBytes(int amount) {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[amount];
		random.nextBytes(bytes);
		
		String str = "";
		for (int i = 0; i < bytes.length; i++) {
			str += Integer.toHexString(bytes[i] & 0xFF);
		}
		return str;
	}

	public static FileHandle dir;
	public static FileHandle getDir(String subdir) {
		if (isAndroid) {
			if (dir == null) {
				dir = Gdx.files.external("shadowenginetest");
			}
			if (subdir == null || subdir.isEmpty()) {
				return dir;
			}
			FileHandle child = dir.child(subdir);
			child.mkdirs();
			return child;
		} else {
			if (dir == null) {
				String path = "";
				try {
					String rawpath = Shadow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					path = URLDecoder.decode(rawpath, "UTF-8");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				dir = Gdx.files.absolute(path).parent();
			}
			dir.mkdirs();
			return dir;
		}
	}
	
	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				try {
					File dir = getDir("logs").file();
					File logfile = new File(dir, "log_"+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+".txt");
					logfile.createNewFile();
					PrintStream fos = new PrintStream(logfile);
					e.printStackTrace(fos);
					fos.close();
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
				System.exit(1);
			}
		};
		
		Thread.currentThread().setDefaultUncaughtExceptionHandler(eh);
		Thread.setDefaultUncaughtExceptionHandler(eh);
		
		dispw = Gdx.graphics.getWidth();
		disph = Gdx.graphics.getHeight();
		
		if (!isAndroid || isOuya) {
			viewmode = 0x02;
		} else {
			viewmode = 0x01;
		}
		viewmode = 0x04;
		
		//Alternate values for view: vieww = 12.5f; viewh = 15f;
		switch (viewmode) {
		case 0x00:
			vieww = dispw/viewff;
			viewh = disph/viewff;
		case 0x01:
			vieww = dispw/viewff;
			break;
		case 0x02:
			viewh = disph/viewff;
			break;
		default:
			break;
		}
		
		touchh = 7f;
		touchw = touchh*dispw/disph;
		
		Gdx.input.setInputProcessor(this);
		controllerHelper = new ControllerHelper();
		Controllers.addListener(controllerHelper);
		Input.setUp();
		Input.keylisteners.add(this);
		
		cam = new Camera();
		resize();
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
		if (Shadow.isAndroid && !Shadow.isOuya) {
			System.exit(0);
		}
		//TODO: save data
		//TODO: cleanup resources
	}

	@Override
	public void render() {
		subtick();
		
		if (loadstate == 0) {
			return;
		}
		
		tick();
		
		if (shader != null) {
			shader.begin();
			shader.setUniformf("resolution", dispw, disph);
			shader.end();
		}
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.setColor(1f, 1f, 1f, 1f);
		ModLoader.preRender();
		if (level != null && (level instanceof MenuLevel)) {
			Input.isInMenu = true;
		} else {
			Input.isInMenu = false;
		}
		cam.render();
		ModLoader.postRender();
		
		long time = System.currentTimeMillis();
		
		frames++;
		eframes++;
		if (lastfmicro == 0) {
			lastfmicro = time;
		}
		if (elastfmicro == 0) {
			elastfmicro = time;
		}
		
		if (time - lastfmicro >= 1000) {
			lastfmicro = time;
			fps = frames;
			frames = 0;
		}
		if (time - elastfmicro >= 100) {
			elastfmicro = time;
			efps = eframes*10;
			eframes = 0;
		}
	}
	
	public void subtick() {
		while (Gdx.graphics == null) {
		}
		if (loadstate == 0) {
			//Gdx.graphics.setVSync(true);
			String shaderDesktop = "shaders/vignette";
			String shaderAndroid = "shaders/basic";
			String shaderOuya = "shaders/vignette";
			String shaderLoad = isOuya?shaderOuya:isAndroid?shaderAndroid:shaderDesktop;
			
			try {
				ShaderProgram.pedantic = false;
				
				//TODO Change / update / fix / complete GLSL shaders
				final String vertex = Gdx.files.internal(shaderLoad+".vert").readString();
				final String fragment = Gdx.files.internal(shaderLoad+".frag").readString();
				
				shader = new ShaderProgram(vertex, fragment);
				
				if (shader.getLog().length()!=0) {
					System.err.println(shader.getLog());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			spriteBatch = new SpriteBatch(4096);
			
			if (shader != null) {
				spriteBatch.setShader(shader);
			}
			
			Images.loadBasic();
			
			loadstate = 1;
			loadtick = 0;
			
			//Gdx.gl.glDisable(GL10.GL_ALPHA_TEST);
			//Gdx.gl.glDisable(GL10.GL_TEXTURE_2D);
			
			//Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
			//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			//cam.render();
		} else if (loadstate == 1) {
			if (loadtick == loadticks[0][0]) {
				//Fonts
				Fonts.load();
			}
			if (loadtick == loadticks[0][1]) {
				//More images
				Images.loadImages();
			}
			if (loadtick == loadticks[0][1]) {
				//Sounds
				Sounds.loadSounds();
			}
			if (loadtick == loadticks[0][3]) {
				//Set up various smaller values, options or machine-dependent stuff.
				if (isAndroid) {
					GameObject.pixffac = 2;
					Level.maxParticles = 128;
				}
			}
			if (loadtick == loadticks[0][4]) {
				if (!isAndroid) {
					//TODO Set up streams
					client = new NetClient();
					server = new NetServer();
				}
			}
			if (loadtick == loadticks[0][5]) {
				//If NOT android load mods.
				ModLoader.initBuiltin();
				if (!isAndroid) {
					String path = "";
					try {
						String rawpath = Shadow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
						path = URLDecoder.decode(rawpath, "UTF-8");
					} catch (Exception e) {
						e.printStackTrace();
					}
					FileHandle fh = Gdx.files.absolute(path).parent();
					ModLoader.init(fh);
					ModLoader.loadResources();
				}
			}
			if (loadtick == loadticks[0][6]) {
				//Jump into first level (TitleLevel).
				level = new TitleLevel();
				loadstate = 2;
			}
			loadtick++;
		}
	}
	
	public void tick() {
		ModLoader.preTick();
		controllerHelper.tick();
		Input.tick();
		Level tmplvl = level;
		if (level != null) {
			level.tick();
		}
		if (server != null) {
			server.tick();
		}
		if (client != null) {
			client.tick();
		}
		ModLoader.postTick();
	}
	
	@Override
	public void resize(int width, int height) {
		resize();
	}
	
	public static void resize() {
		if (Gdx.graphics != null) {
			dispw = Gdx.graphics.getWidth();
			disph = Gdx.graphics.getHeight();
			
			switch (viewmode) {
			case 0x00:
				break;
			case 0x01:
				viewh = vieww*disph/dispw;
				break;
			case 0x02:
				vieww = viewh*dispw/disph;
				break;
			case 0x03:
				vieww = dispw/viewff;
				viewh = disph/viewff;
				break;
			case 0x04:
				vieww = dispw/viewff;
				viewh = disph/viewff;
				if (isAndroid || (dispw/viewff >= 21.75f && disph/viewff >= 18f)) {
					vieww = vieww/1.5f;
					viewh = viewh/1.5f;
				}
				break;
			default:
				break;
			}
			
			touchw = touchh*dispw/disph;
			cam.resize();
			Input.resize();
			
		}
	}

	@Override
	public void pause() {
		gdxpaused = true;
	}

	@Override
	public void resume() {
		gdxpaused = false;
	}

	@Override
	public boolean keyDown(int keycode) {
		boolean handle = false;
		for (Input.Key k : Input.all) {
			for (int id : k.keyid) {
				if (id == keycode) {
					k.triggerer = Triggerer.KEYBOARD;
					k.nextState = true;
					handle = true;
				}
			}
		}
		return handle;
	}

	@Override
	public boolean keyUp(int keycode) {
		boolean handle = false;
		for (Input.Key k : Input.all) {
			for (int id : k.keyid) {
				if (id == keycode) {
					k.triggerer = Triggerer.KEYBOARD;
					k.nextState = false;
					handle = true;
				}
			}
		}
		return handle;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;
	}
	
	float hxoffs = 0;
	float hyoffs = 0;
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean handle = false;
		if (!Input.isAndroid) {
			pointer = -1;
		}
		//System.out.println("X: "+screenX+"; Y: "+screenY+"; P: "+pointer+"; B: "+button+"; M: D");
		if (Input.isAndroid && !Input.isOuya && !Input.isInMenu) {
			for (Input.Key k : Input.all) {
				if (k.rec.contains(screenX, screenY)) {
					k.triggerer = Triggerer.SCREEN;
					k.nextState = true;
					k.pointer = pointer;
					TouchPoint tp = new TouchPoint(screenX, screenY, pointer, button, TouchMode.KeyInput);
					if (Input.touches.containsValue(pointer, true)) {
						Input.touches.remove(pointer);
					}
					Input.touches.put(pointer, tp);
					handle = true;
				}
			}
		}
		
		if (!handle && level != null && level.ready && cam.camrec != null) {
			TouchPoint tp = new TouchPoint(screenX, screenY, pointer, button, TouchMode.Cursor);
			Input.touches.put(pointer, tp);
			handle = true;
		}
		
		return handle;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean handle = true;
		if (!Input.isAndroid) {
			pointer = -1;
		}
		if (Input.isAndroid && !Input.isOuya && !Input.isInMenu) {
			//System.out.println("X: "+screenX+"; Y: "+screenY+"; P: "+pointer+"; B: "+button+"; M: U");
			for (Input.Key k : Input.all) {
				if (k.rec.contains(screenX, screenY)) {
					k.triggerer = Triggerer.SCREEN;
					k.nextState = false;
					k.pointer = -2;
				}
			}
		}
		
		Input.touches.remove(pointer);
		
		return handle;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean handle = true;
		if (!Input.isAndroid) {
			pointer = -1;
		}
		TouchPoint tp = Input.touches.get(pointer);
		if (tp != null) {
			tp.pos.set(screenX, screenY);
			if (Input.isAndroid && !Input.isOuya && !Input.isInMenu && tp.touchmode == TouchMode.KeyInput) {
				//System.out.println("X: "+screenX+"; Y: "+screenY+"; P: "+pointer+"; M: D");
				for (Input.Key k : Input.all) {
					if (k.pointer == pointer && !k.rec.contains(screenX, screenY)) {
						k.triggerer = Triggerer.SCREEN;
						k.nextState = false;
						k.pointer = -2;
					}
				}
				for (Input.Key k : Input.all) {
					if (k.rec.contains(screenX, screenY)) {
						k.triggerer = Triggerer.SCREEN;
						k.nextState = true;
						k.pointer = pointer;
					}
				}
			}
		}
		
		return handle;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		boolean handled = false;
		if (level != null && level.c != null) {
			TouchPoint tp = null;
			Garbage.vec2.x = screenX;
			Garbage.vec2.y = screenY;
			level.c.pos.set(level.c.calcPos(Garbage.vec2));
			level.c.render = true;
			handled = true;
		}
		return handled;
	}

	@Override
	public boolean scrolled(int amount) {
		boolean handled = false;
		if (level != null && level.c != null) {
			level.c.scroll(amount);
			handled = true;
		}
		return handled;
	}
	
	@Override
	public void keyDown(Key key) {
		if (key == Input.pause || key == Input.androidBack || key == Input.androidMenu) {
			if (!(level instanceof MenuLevel) && (isAndroid || key == Input.pause)) {
				MenuLevel pause = new PauseLevel();
				pause.bglevel = level;
				level = pause;
			} else if (isAndroid && level instanceof PauseLevel && key == Input.androidBack) {
				level = new TitleLevel();
			} else if (isAndroid && level instanceof TitleLevel && key == Input.androidBack){
				Gdx.app.exit();
			} else if (level instanceof MenuLevel && key == Input.androidBack) {
				MenuLevel ml = (MenuLevel) level;
				if (ml.parent != null) {
					level = ml.parent;
				} else if (ml.bglevel != null) {
					level = ml.bglevel;
				}
			} 
		}
		if (key == Input.screenshot) {
			Pixmap pixmap = Camera.getScreenshot(0, 0, (int)dispw, (int)disph, false);
			
			try {
				FileHandle fh = null;
				fh = getDir("screenshots").child("screen_"+(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()))+".png");
				fh.parent().mkdirs();
				
				PixmapIO.writePNG(fh, pixmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			pixmap.dispose();
		}
	}

	@Override
	public void keyUp(Key key) {
	}
	
}
