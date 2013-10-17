package net.fourbytes.shadow.utils;

public final class ViewModes {
	private ViewModes() {
	}

	/*
	 * 0x00 = Fully dynamic (Useless, ugly, ...) <br>
	 * 0x01 = Fixed height (Mobile devices, small screens) <br>
	 * 0x02 = Fixed width (PC and Ouya, larger resolutions) <br>
	 * 0x03 = Fully fixed (Resizing doesn't scale) <br>
	 * 0x04 = Automatic scaling (Does what it says) <br>
	 * Other: Gliatch.
	 */

	/**
	 * Fully dynamic scaling. Useless and ugly.
	 */
	public final static byte dynamic = 0x00;
	/**
	 * Fixed height scaling. Mobile devices and small screens may profit from it.
	 */
	public final static byte fixedh = 0x01;
	/**
	 * Fixed width. PC and Ouya, thus larger screens may profit from it.
	 */
	public final static byte fixedw = 0x02;
	/**
	 * Fully fixed viewport. It doesn't scale at all (stays always the same).
	 */
	public final static byte fixed = 0x03;
	/**
	 * Automatic scaling as soon as (dispw+disph)/2f is over a given value.
	 */
	public final static byte auto = 0x04;

	/**
	 * Default scaling method. It's NOT the current scaling method.
	 */
	public final static byte def = auto;

}
