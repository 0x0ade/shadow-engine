package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.systems.DefaultSystemManager;
import net.fourbytes.shadow.systems.ISystemManager;
import net.fourbytes.shadow.utils.AsyncThread;
import net.fourbytes.shadow.utils.Options;

/**
 * A level containing an loading screen.
 */
public class LoadingLevel extends Level {

	/**
	 * Steps (Runnables) to run in order.
	 */
	public Runnable[] steps;
	public int step = 0;
	protected AsyncThread thread;

	public float progress = 0;
	public float progressMax = -1;
	protected float fOld = 0f;
	protected boolean fInverse = false;

	public Level bglevel;
	public boolean bgpaused = true;
	public Color dimm = new Color(0f, 0f, 0f, 0.3f);
	public Image dimmimg;

	protected boolean omitloop = false;

	@Override
	public ISystemManager createSystems() {
		ISystemManager systems = new DefaultSystemManager(this);
		return systems;
	}

	public void start() {
		//check if AT already running
		if (thread != null && !thread.finished) {
			throw new IllegalStateException(getClass().getSimpleName()+" already running!");
		}

		//create new AT if there isn't an already existing one
		if (thread == null) {
			thread = new AsyncThread();
		}

		//reset current step
		step = 0;

		//reset the AT's current step
		thread.current = 0;

		//set the (fresh) AT to wait forever until manually killed
		if (thread.timeLast == 0L) {
			thread.time = 0L;
		}

		//run the AT in the background
		thread.start();
	}

	public void stop() {
		//let the AT "finish" the current task (if any) and then die
		thread.finished = true;
		if (bglevel != null && Shadow.level == this) {
			Shadow.level = bglevel;
		}
		Shadow.cam.firsttick = true;
	}

	@Override
	public void tick(float delta) {
		if (bglevel != null) {
			boolean lastInteract = true;
			if (bglevel.player != null) {
				player = bglevel.player;
				if (Shadow.cam.player != player) {
					Shadow.cam.player = player;
					Shadow.cam.firsttick = true;
				}
				lastInteract = bglevel.player.canInteract;
				bglevel.player.canInteract = false;
			}
			bglevel.paused = bgpaused;
			bglevel.tick(delta);
			bglevel.paused = false;
			if (bglevel.player != null) {
				bglevel.player.canInteract = lastInteract;
			}
		} else {
			player = null;
		}

		if (thread == null || thread.finished) {
			return;
		}

		if (thread.left == 0) {
			if (step >= steps.length) {
				stop();
				return;
			}
			thread.queue(steps[step]);
			step++;
		}
	}

	@Override
	public void renderImpl() {
		if (bglevel != null && !omitloop) {
			omitloop = true;
			if (bgpaused) {
				bglevel.canRenderImpl = false;
				Shadow.cam.level = false;
			}
			Shadow.cam.renderLevel(bglevel);
			if (bgpaused) {
				Shadow.cam.level = true;
				bglevel.canRenderImpl = true;
			}
		}
		omitloop = false;

		Rectangle vp = Shadow.cam.camrec;
		if (!Options.getBoolean("gfx.blur", true) || bglevel == null) {
			if (dimmimg == null) {
				dimmimg = Images.getImage("white");
			}
			dimmimg.setPosition(vp.x, vp.y + vp.height);
			dimmimg.setSize(1f, -1f);
			dimmimg.setScale(vp.width, vp.height);
			dimmimg.setColor(dimm);
			dimmimg.draw(Shadow.spriteBatch, 1f);
		}

		if (bglevel == null) {
			Image logo = Images.getImage("logo");
			logo.setScale(Shadow.vieww / Shadow.dispw * Shadow.cam.cam.zoom, -Shadow.viewh / Shadow.disph * Shadow.cam.cam.zoom);
			logo.setPosition(0f - (logo.getScaleX() * logo.getWidth()) / 2f, 0f - (logo.getScaleY() * logo.getHeight()) / 2f);
			logo.draw(Shadow.spriteBatch, 1f);
		}

		float progress = this.progress;
		float progressMax = this.progressMax;

		if (progressMax < 0f) {
			progress = (float) step;
			progressMax = (float) steps.length;
		}

		Vector3 campos = Shadow.cam.cam.position;

		if (progress == progressMax && progressMax == 0f) {
			float f = 1f;
			f -= (f - fOld) * 0.9f;
			fOld = f;

			if (!fInverse) {
				Shadow.spriteBatch.setColor(0f, 0f, 0f, 1f);
			} else {
				Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
			}
			Shadow.spriteBatch.draw(Shadow.cam.white,
					campos.x - Shadow.vieww / 4f, campos.y + Shadow.viewh / 4f + 2f,
					Shadow.vieww / 2f, 0.5f);
			if (!fInverse) {
				Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
			} else {
				Shadow.spriteBatch.setColor(0f, 0f, 0f, 1f);
			}
			Shadow.spriteBatch.draw(Shadow.cam.white,
					campos.x - Shadow.vieww / 4f, campos.y + Shadow.viewh / 4f + 2f,
					(Shadow.vieww / 2f) * f, 0.5f);

			if (f >= 0.985f) {
				fOld = 0f;
				fInverse = !fInverse;
			}
		} else {
			float f = progress / progressMax;
			f -= (f - fOld) * 0.9f;
			fOld = f;

			Shadow.spriteBatch.setColor(0f, 0f, 0f, 1f);
			Shadow.spriteBatch.draw(Shadow.cam.white,
					campos.x - Shadow.vieww / 4f, campos.y + Shadow.viewh / 4f + 2f,
					Shadow.vieww / 2f, 0.5f);
			Shadow.spriteBatch.setColor(1f, 1f, 1f, 1f);
			Shadow.spriteBatch.draw(Shadow.cam.white,
					campos.x - Shadow.vieww / 4f, campos.y + Shadow.viewh / 4f + 2f,
					(Shadow.vieww / 2f) * f, 0.5f);
		}
	}

}
