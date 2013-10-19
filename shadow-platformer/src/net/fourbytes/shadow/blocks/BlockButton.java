package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;

public class BlockButton extends BlockType implements BlockLogic {
	
	@Saveable
	boolean triggered = false;
	@Saveable
	int mode = 0;
	
	public BlockButton() {
	}
	
	public BlockButton(int mode) {
		this.mode = mode;
	}
	
	@Override 
	public void tick() {
		block.interactive = true;
		block.solid = true;
		block.colloffs.set(0f, 9f/16f, 0f, 1f-(7f/16f));
	}
	
	@Override
	public TextureRegion getTexture(int id) {
		TextureRegion[][] regs = Images.split("block_button", 16, 16);
		TextureRegion reg = null;
		reg = regs[0][triggered?1:0];
		return reg;
	}
	
	@Override
	public void collide(Entity e) {
		super.collide(e);
		if (e instanceof Player) {
			Player p = (Player) e;
			
			if (mode == 1) {
				p.spawnpos.set(p.pos);
			}
			
			//TODO: only trigger if player is ON the button
			if (triggered == false && p.pos.y < block.pos.y-0.1f) {
				Sounds.getSound("button_ingame").play(1f, Sounds.calcPitch(1f, 0.2f), 0f);
				block.imgupdate = true;
				block.imgupdate = true;
				triggered = true;
				
				for (Layer l : block.layer.level.layers.values()) {
					Array<Block> v = l.get(Coord.get(block.pos.x, block.pos.y));
					if (v != null) {
						for (Block b : v) {
							if (b instanceof TypeBlock) {
								TypeBlock tb = (TypeBlock) b;
								BlockType type = tb.type;
								if (type instanceof BlockLogic) {
									BlockLogic bl = (BlockLogic) type;
									bl.handle(triggered);
								}
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
