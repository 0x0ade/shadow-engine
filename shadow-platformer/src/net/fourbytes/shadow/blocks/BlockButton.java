package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;

public class BlockButton extends BlockType implements BlockLogic {
	
	@IsSaveable
	public boolean triggered = false;
	@IsSaveable
	public int mode = 0;
	
	public BlockButton() {
	}
	
	public BlockButton(int mode) {
		this.mode = mode;
	}
	
	@Override 
	public void init() {
		tickAlways = true;
		solid = true;
		colloffs.set(0f, 9f/16f, 0f, 1f-(7f/16f));
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		return Images.split("block_button", 16, 16)[0][triggered?1:0];
	}

	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (mode == 1) {
				p.spawnpos.set(p.spawnpos);
			}
			
			//TODO: only trigger if player is ON the button
			if (!triggered) {
				Sounds.getSound("button_ingame").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
				texupdate = true;
				triggered = true;
				
				for (Layer l : layer.level.layers.values()) {
					Array<Block> v = l.get(Coord.get(pos.x, pos.y));
					if (v != null) {
						for (int i = 0; i < v.size; i++) {
							Block b = v.items[i];
							if (b instanceof BlockLogic) {
								BlockLogic bl = (BlockLogic) b;
								bl.handle(triggered);
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean triggered() {
		return triggered;
	}

	@Override
	public void handle(boolean triggered) {
		//this.triggered = triggered;
	}

	@Override
	public LogicType getType() {
		return LogicType.OUTPUT;
	}
	
}
