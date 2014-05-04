package net.fourbytes.shadow;

/**
 * Fast, primitive (taken seriously) coordinate class.
 * Coord converts two ints (X and Y) into one long (C) and
 * C back to X and Y. This class also is able to multiply
 * and divide coordinates (by ints only due to possible rounding
 * error). Due to rounding errors outside of the Coord
 * class, the get1337 method is able to "fix" these by
 * adding 1 to any value larger than 0.
 */
public final class Coord {
	private Coord() {
	}
	
	public final static long get(int x, int y) {
		return (long) x << 32 | y & 0xFFFFFFFFL;
	}
	
	public final static long get(float x, float y) {
		return (long)((int)x) << 32 | ((int)y) & 0xFFFFFFFFL;
	}
	
	public final static int[] getXY(long c) {
		return new int[] {(int) (c >> 32), (int) (c)};
	}
	
	public final static int getX(long c) {
		return (int) (c >> 32);
	}
	
	public final static int getY(long c) {
		return (int) (c);
	}
	
	/**
	 * H4CK3D H04X F7W. 7R0L0L0L0L0~~<br>
	 * Seriously: Don't use hacked hoaxes. You will regret it.
	 */
	public final static int get1337(int x) {
		if (x > 0) {//TODO Check if >= 0 or >.
			x++;
		}
		return x;
	}

	public final static long mul(long c, int x) {
		return (long) (((int) (c >> 32)) * x) << 32 | (((int) (c)) * x) & 0xFFFFFFFFL;
	}

	public final static long mul(long c, int x, int y) {
		return (long) (((int) (c >> 32)) * x) << 32 | (((int) (c)) * y) & 0xFFFFFFFFL;
	}

	public final static long div(long c, int x) {
		return (long) (((int) (c >> 32)) / x) << 32 | (((int) (c)) / x) & 0xFFFFFFFFL;
	}

	public final static long div(long c, int x, int y) {
		return (long) (((int) (c >> 32)) / x) << 32 | (((int) (c)) / y) & 0xFFFFFFFFL;
	}
	
}
