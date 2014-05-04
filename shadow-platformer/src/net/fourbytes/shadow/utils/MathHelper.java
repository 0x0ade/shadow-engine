package net.fourbytes.shadow.utils;

public class MathHelper {

	public static float dist(float x1, float y1, float x2, float y2) {
		float distx = x2 - x1;
		float disty = y2 - y1;
		return (float) Math.sqrt(distx*distx + disty * disty);
	}
	
	public static float distsq(float x1, float y1, float x2, float y2) {
		float distx = x2 - x1;
		float disty = y2 - y1;
		return distx*distx + disty * disty;
	}
	
	public static int pot(int x) {
		return 1 << (32 - Integer.numberOfLeadingZeros(x-1));
	}

}
