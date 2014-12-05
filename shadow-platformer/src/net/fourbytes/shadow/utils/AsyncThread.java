package net.fourbytes.shadow.utils;

import com.badlogic.gdx.math.MathUtils;

/**
 * AsyncThreads are threads running queued operations to reduce load on the
 * main thread. AsyncThread instances also optionally stop listening for new
 * operations and thus exit after given time elapsed.
 */
public class AsyncThread extends Thread {

	public long time = 0L;
	public long timeLast = 0L;
	public Runnable[] queued = new Runnable[2048];//TODO resize dynamically
	public int currentQueue = 0;
	public int current = 0;
	public int left = 0;
	public boolean finished = false;

	public AsyncThread() {
		this("AsyncThread "+MathUtils.random(65536), 10000L);
	}

	public AsyncThread(String name) {
		this(name, 10000L);
	}

	public AsyncThread(int time) {
		this("AsyncThread "+MathUtils.random(65536), 10000L);
	}

	public AsyncThread(String name, long time) {
		super(name);

		this.time = time;
	}

	@Override
	public void run() {
		finished = false;
		timeLast = System.currentTimeMillis();
		current = 0;
		while ((time <= 0L && !finished) || timeLast + time > System.currentTimeMillis()) {
			Runnable run = queued[current];
			if (run != null) {
				try {
					run.run();
				} catch (Throwable t) {
					t.printStackTrace();
				}
				queued[current] = null;
				timeLast = System.currentTimeMillis();
				current = (current+1)%queued.length;
				left--;
			}
			Thread.yield();
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		finished = true;
	}

	public void queue(Runnable run) {
		left++;
		queued[currentQueue] = run;
		currentQueue = (currentQueue+1)%queued.length;
	}
}
