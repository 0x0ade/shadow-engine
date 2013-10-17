package net.fourbytes.shadow.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.Input.TouchPoint;
import net.fourbytes.shadow.Input.TouchPoint.TouchMode;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.utils.gdx.ByteMap;

public class Cursor extends Entity {
	
	public int id = -1;
	public boolean render = false;
	Color color;

	public byte layerid = 0;
	public byte playerln = Byte.MIN_VALUE;
	
	public Cursor(Vector2 position, Layer layer) {
		this(position, layer, -1);
	}
	
	public Cursor(Vector2 position, Layer layer, int id) {
		super(position, layer);
		this.id = id;
		solid = false;
		color = new Color(1f, 1f, 1f, 0.5f);
	}
	
	@Override
	public TextureRegion getTexture() {
		return Images.getTextureRegion("white");
	}
	
	int button;
	
	public void tick() {
		if (playerln == Integer.MIN_VALUE) {
			for (ByteMap.Entry entry : layer.level.layers.entries()) {
				byte ln = entry.key;
				Layer ll = (Layer) entry.value;

				if (ll.entities.contains(layer.level.player, true)) {
					playerln = ln;
					break;
				}
			}
		}

		layer.level.fillLayer(playerln + layerid);
		layer = layer.level.layers.get(playerln + layerid);

		TouchPoint tp = Input.touches.get(id);
		if (tp != null && tp.touchmode == TouchMode.Cursor) {
			pos.set(calcPos(tp.pos));
			oldpos = tp.pos;
			
			button = tp.button;
			
			switch (tp.button) {
			case -1: amb(tp, true); break;
			case 0: lmb(tp, true); break;
			case 1: rmb(tp, true); break;
			case 2: mmb(tp, true); break;
			}
			
			render = true;
		} else {
			downtick = 0;
			switch (button) {
			case -1: amb(tp, false); break;
			case 0: lmb(tp, false); break;
			case 1: rmb(tp, false); break;
			case 2: mmb(tp, false); break;
			}
		}
		if (tp == null && id != -1) {
			layer.level.cursors.removeValue(this, true);
		}
		if (oldpos != null && id == -1) {
			pos.set(calcPos(oldpos));
		}
		scroll(0);
	}
	
	int downtick = 0;
	boolean amb = false;
	int amode = 0;
	boolean lmb = false;
	boolean rmb = false;
	boolean mmb = false;
	
	public void amb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && !amb) {
			amode = 0;
		}
		if (isDown && amb) {
			switch (amode) {
			case 0: break;
			case 1: lmb(point, true); break;
			case 2: rmb(point, true); break;
			}
		}
		amb = isDown;
	}
	
	public void lmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!lmb || downtick > 20)) {
			Block b = BlockType.getInstance("BlockPush", pos.x, pos.y, layer);
			b.layer.add(b);
		}
		lmb = isDown;
	}
	
	public void rmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!rmb || downtick > 20)) {
			/*Array<Block> blocks = layer.get(Coord.get(pos.x, pos.y));
			if (blocks != null) {
				for (Block b : blocks) {
					b.layer.remove(b);
				}
			}*/
			Entity e = new MobTest(new Vector2(pos), layer);
			e.layer.add(e);
		}
		rmb = isDown;
	}
	
	public void mmb(TouchPoint point, boolean isDown) {
		downtick++;
		if (isDown && (!mmb || downtick > 20)) {
			Block b = BlockType.getInstance("BlockWater", pos.x, pos.y, layer);
			b.layer.add(b);
		}
		mmb = isDown;
	}
	
	static Vector2 oldpos = new Vector2();
	final static Vector2 ppos = new Vector2();
	
	public Vector2 calcPos(Vector2 apos) {
		oldpos.set(apos);
		Vector2 pos = ppos;
		pos.set(apos);
		float tx = 0;
		float ty = 0;
		float g = 1f;
		float cx = Shadow.cam.camrec.x;
		float cy = Shadow.cam.camrec.y;
		float mx = (pos.x * (Shadow.vieww/Shadow.dispw)) * Shadow.cam.cam.zoom;
		float my = (pos.y * (Shadow.viewh/Shadow.disph)) * Shadow.cam.cam.zoom;
		tx = mx + cx;
		ty = my + cy;
		float otx = tx;
		float oty = ty;
		tx = (int)(tx/g);
		ty = (int)(ty/g);
		tx *= g;
		ty *= g;
		if (otx < 0) {
			tx-=g;
		}
		if (oty < 0) {
			ty-=g;
		}
		pos.set(tx, ty);
		return pos;
	}
	
	@Override
	public void preRender() {
		super.preRender();
		tmpimg.setColor(color);
		if (!render) {
			tmpimg.setColor(1f, 1f, 1f, 0f);
		}
	}
	
	@Override
	public void render() {
		if (render) {
			super.render();
		}
	}

	public void scroll(int amount) {
	}
	
}
