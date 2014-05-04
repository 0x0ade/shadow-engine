package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import net.fourbytes.shadow.Block;
import net.fourbytes.shadow.Layer;
import net.fourbytes.shadow.mod.ModManager;

import java.lang.reflect.Constructor;

public abstract class BlockType extends Block {
	
	public static enum LogicType {
		INPUT,
		OUTPUT,
		PUTPUT
	}
	
	public String[] attr;

	public BlockType() {
		super(new Vector2(), null);
	}
	
	public static BlockType getInstance(String subtype, float x, float y, Layer layer) {
		BlockType b = ModManager.getTypeBlock(subtype, x, y, layer);
		if (b != null) {
			if (b.subtype == null || b.subtype.isEmpty()) {
				b.subtype = subtype;
			}
			return b;
		}
		try {
			String classname = subtype;
			Object[] args = new Object[0];
			Class<?>[] argtypes = new Class<?>[0];
			if (subtype.contains(".")) {
				String[] split = subtype.split("\\.");
				classname = split[0];
				args = new Object[split.length-1];
				argtypes = new Class[args.length];
				for (int i = 0; i < args.length; i++) {
					try {
						args[i] = Integer.parseInt(split[i+1]);
						argtypes[i] = int.class;
					} catch (Throwable e) {
						try {
							args[i] = Float.parseFloat(split[i+1]);
							argtypes[i] = float.class;
						} catch (Throwable e1) {
							args[i] = split[i+1].replace("\\|", "\\.");
							argtypes[i] = String.class;
						}
					}
					if (argtypes[i] == null) {
						argtypes[i] = args[i].getClass();
					}
				}
			}
			Class<?> c = ClassReflection.forName("net.fourbytes.shadow.blocks." + classname);
			/*String tmp = "";
			for (Object o : args) {
				tmp += o+" ";
			}
			if (!tmp.isEmpty()) {
				System.out.println(tmp);
			}*/
			Constructor<?> constr = c.getConstructor(argtypes);
			constr.setAccessible(true);
			BlockType block = (BlockType) constr.newInstance(args);
			block.subtype = subtype;
			block.pos.set(x, y);
			block.layer = layer;
			block.init();
			return block;
		} catch (Throwable t) {
			String imgname = subtype.toLowerCase();
			if (imgname.startsWith("block")) {
				imgname = imgname.substring(5);
			}
			imgname = "block_"+imgname;
			BlockType block = new BlockImage(imgname);
			block.subtype = subtype;
			block.pos.set(x, y);
			block.layer = layer;
			block.init();
			return block;
		}
	}

	public void init() {
	}
	
}
