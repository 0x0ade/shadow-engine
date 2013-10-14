package net.fourbytes.shadow.utils;

import com.badlogic.gdx.math.Rectangle;

public class MathHelper {
	public static float dist(float x1,float y1, float x2, float y2) {
		return (float) Math.sqrt(sq(x2-x1)+sq(y2-y1));
	}
	
	public static float distsq(float x1,float y1, float x2, float y2) {
		return (sq(x2-x1)+sq(y2-y1));
	}
	
	public static float sq(float a) {
		return a*a;
	}

	public static boolean overlaps(Rectangle or, Rectangle er) {
		return or.x <= er.x + er.width && or.x + or.width >= er.x && or.y <= er.y + er.height && or.y + or.height >= er.y;
	}

	public static int pot(int x) {
		return 1 << (32 - Integer.numberOfLeadingZeros(x-1));
	}
}
