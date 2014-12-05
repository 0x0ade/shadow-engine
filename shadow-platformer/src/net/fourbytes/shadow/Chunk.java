package net.fourbytes.shadow;

import com.badlogic.gdx.utils.Array;

public class Chunk {

	public static int size = 8;

	public final int x;
	public final int y;
	public final long c;
	public final Layer layer;

	public Array<Block> blocks = new Array<Block>(false, 4096, Block.class);
	public Array<Entity> entities = new Array<Entity>(false, 512, Entity.class);
	public Array<Particle> particles = new Array<Particle>(false, 512, Particle.class);

	public int dirty = 0;
	public boolean rerender = true;

	public Chunk(int x, int y, Layer layer) {
		this.x = x;
		this.y = y;
		this.c = Coord.get(x, y);
		this.layer = layer;
	}

	public void dirtify() {
		if (layer.level.dirtify) {
			dirty++;
		}
	}
}
