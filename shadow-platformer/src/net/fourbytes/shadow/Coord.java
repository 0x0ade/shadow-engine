package net.fourbytes.shadow;

public class Coord {
	
	public static long get(int x, int y) {
		return (long)x << 32 | y & 0xFFFFFFFFL;
	}
	
	public static long get(float x, float y) {
		return get((int) x, (int) y);
	}
	
	public static int[] getXY(long c) {
		return new int[] {getX(c), getY(c)};
	}
	
	public static int getX(long c) {
		return (int) (c >> 32);
	}
	
	public static int getY(long c) {
		return (int) (c);
	}
	
	/**
	 * H4CK3D H04X F7W. 7R0L0L0L0L0~~<br>
	 * Seriously: Don't use hacked hoaxes. You will regret it.
	 */
	public static int get1337(int x) {
		if (x > 0) {//TODO Check if >= 0 or >.
			x++;
		}
		return x;
	}

	public static long mul(long c, int x) {
		return mul(c, x, x);
	}

	public static long mul(long c, int x, int y) {
		return Coord.get(Coord.getX(c)*x, Coord.getY(c)*y);
	}

	public static long div(long c, int x) {
		return div(c, x, x);
	}

	public static long div(long c, int x, int y) {
		return Coord.get(Coord.getX(c)/x, Coord.getY(c)/y);
	}
	
}
