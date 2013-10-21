package net.fourbytes.shadow.genlevel;

public abstract class CaveGen {

	public GenLevel level;

	public CaveGen(GenLevel level) {
		this.level = level;
	}

	public void generateFG(int xx, int x, int y, int fg) {
		String got = getBlock(xx, x, y, true);
		handle(xx, x, y, fg, got);
	}

	public void generateBG(int xx, int x, int y, int fg) {
		String got = getBlock(xx, x, y, false);
		handle(xx, x, y, fg-1, got);
	}

	public abstract String getBlock(int xx, int x, int y, boolean isFG);
	public abstract void handle(int xx, int x, int y, int ln, String got);
}
