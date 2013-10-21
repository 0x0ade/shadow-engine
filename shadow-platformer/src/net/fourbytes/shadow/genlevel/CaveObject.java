package net.fourbytes.shadow.genlevel;

public class CaveObject {

	public String type;
	public float probability;
	public int depth;

	public CaveObject() {
		this("", 0, 0);
	}

	public CaveObject(String type, float probability, int depth) {
		this.type = type;
		this.probability = probability;
		this.depth = depth;
	}

}
