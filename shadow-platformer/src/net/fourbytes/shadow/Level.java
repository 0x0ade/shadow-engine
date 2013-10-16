package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Particle;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.utils.gdx.ByteMap;

public class Level {
	
	public ByteMap<Layer> layers = new ByteMap<Layer>(16);
	public Layer mainLayer = new Layer(this);
	public LightSystem lights = new LightSystem(this);
	public TimeDaySystem timeday = new TimeDaySystem(this);
	public Color globalLight = new Color(1f, 1f, 1f, 1f);
	public Player player;
	public Cursor c;
	public Array<Cursor> cursors = new Array<Cursor>();
	@Saveable
	public float gravity = 0.02f;
	@Saveable
	public float xgravity = 0.0f;
	
	public static int maxParticles = 512;
	public static float inviewf = 5f;

	//Abstract constructor, can be used for non-gameplay levels (menus).
	public Level() {
		ready = true;
	}
	
	@Saveable
	public int tiledw = -1;
	@Saveable
	public int tiledh = -1;
	@Saveable
	public String nextlvl;

	@Saveable
	public boolean hasvoid = true;
	public boolean ready = false;
	public boolean paused = false;
	public boolean ftick = true;
	public long tickid = 0;

	
	public Level(String name) {
		if (Gdx.files.internal("data/levels/"+name+".smf").exists()) {
			ShadowMap map = ShadowMap.loadFile(Gdx.files.internal("data/levels/"+name+".smf"));
			map.fillLevel(this);
		} else if (Gdx.files.internal("data/levels/"+name+".tmx").exists()) {
			try {
				TmxMapLoader tml = new TmxMapLoader();
				//TODO Custom TmxMapLoader that loads TiledMaps without loading tileset images
				initTilED(tml.load("data/levels/"+name+".tmx"));
				ready = true;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else {
			System.err.println("Map not found: "+name);
		}

		fillLayer(0);
		c = new Cursor(new Vector2(0f, 0f), layers.get(0));

		System.gc();

		ready = true;
	}
	
	public void fillLayer(int key) {
		if (!layers.containsKey(key)) {
			layers.put(key, new Layer(this));
		}
	}
	
	public void initTilED(TiledMap map) {
		map.dispose();
		//tiledw = map.width;
		//tiledh = map.height;
		nextlvl = (String) map.getProperties().get("nextlvl");
		if (map.getProperties().get("hasvoid") != null && !((String) map.getProperties().get("hasvoid")).isEmpty()) {
			hasvoid = Boolean.parseBoolean((String) map.getProperties().get("hasvoid"));
		}
		for (int ln = 0; ln < map.getLayers().getCount(); ln++) {
			Layer ll = new Layer(this);
			layers.put(ln, ll);
			TiledMapTileLayer l = (TiledMapTileLayer) map.getLayers().get(ln);
			if (l.getWidth() > tiledw) {
				tiledw = l.getWidth();
			}
			if (l.getHeight() > tiledh) {
				tiledh = l.getHeight();
			}
			for(int x = 0; x < l.getWidth(); x++) {
				for(int y = 0; y < l.getHeight(); y++) {
					Cell cell = l.getCell(x, y);
					if (cell != null && cell.getTile() != null) {
						GameObject obj = getGameObject(ln, x, l.getHeight()-y, cell);
						if (obj instanceof Block) {
							ll.add((Block) obj);
						} else if (obj instanceof Entity) {
							ll.add((Entity) obj);
						}
					}
				}
			}
		}
	}
	
	GameObject getGameObject(int ln, int x, int y, Cell cell) {
		int tid = cell.getTile().getId();
		GameObject obj = null;
		String type = (String) cell.getTile().getProperties().get("type");
		String subtype = (String) cell.getTile().getProperties().get("subtype");
		
		obj = ShadowMap.convert(x, y, layers.get(ln), tid, type, subtype);
		
		if (obj instanceof Player) {
			player = (Player) obj;
		}
		
		return obj;
	}

	public void tick() {
		tickid++;

		mainLayer.inView.clear();
		for (Layer ll : layers.values()) {
			ll.inView.clear();
		}

		for (Block block : mainLayer.blocks) {
			if (block == null) continue;
			objrec.set(block.pos.x-inviewf, block.pos.y-inviewf, block.rec.width+inviewf*2f, block.rec.height+inviewf*2f);

			if (Shadow.cam.camrec.overlaps(objrec)) {
				mainLayer.inView.add(block);
				block.layer.inView.add(block);
			}

			if ((Shadow.cam.camrec.overlaps(objrec) || block.interactive || ftick) && !paused) {
				block.tick();
			}
		}
		
		int particle = 0;
		Array<Particle> particles = Garbage.particles;
		particles.clear();

		for (Entity entity : mainLayer.entities) {
			if (entity == null) continue;
			if (entity instanceof Particle) {
				if (!((Particle)entity).isStatic) {
					particles.add((Particle)entity);
					if (particle >= maxParticles) {
						mainLayer.remove(particles.get(0));
						particles.removeIndex(0);
						continue;
					}
					particle++;
				}
			}

			objrec.set(entity.pos.x-inviewf, entity.pos.y-inviewf, entity.rec.width+inviewf*2f, entity.rec.height+inviewf*2f);
			if (Shadow.cam.camrec.overlaps(objrec)) {
				mainLayer.inView.add(entity);
				entity.layer.inView.add(entity);
			}

			if (!paused){
				entity.tick();
			}
		}

		if (!paused) {
			if (timeday != null) {
				timeday.tick();
			}

			if (Shadow.level == this) {
				for (Cursor c : cursors) {
					c.tick();
				}
				c.tick();
				if (pointblock != null) {
					pointblock.tick();
				}
			}
		}

		ftick = false;
		
		//System.out.println("Blocks: "+nblocks+"; Entities: "+nentities);
	}
	
	protected static Rectangle objrec = new Rectangle();
	
	public boolean canRenderImpl = true;
	public static BitmapFont font = Fonts.light_normal;
	protected static Image bgwhite;
	protected static Image fgwhite;
	protected Block pointblock;
	
	public void renderImpl() {
		if (!canRenderImpl) return;
		Rectangle vp = Shadow.cam.camrec;
		
		/*
			img.setScale((1f/16f));
			img.setPosition(vp.x + Shadow.vieww/2 -(1.25f*(2.5f-i)) - 0.125f, vp.y + 1.5f);
			img.draw(Shadow.spriteBatch, 0.9f);
		*/
		
		/*
		//OUTDATED: Health symbols / hearts 
		//SEMI_TODO: Fix accurracy. 
		for (int i = 0; i < hnum; i++) {
			Image img = null;
			img = hf;
			System.out.println(player.health);
			System.out.println(i);
			if (((int)(10*player.health))<=i) {
				img = he;
			}
			img.setScale((1f/24f));
			img.setScaleY(-img.getScaleY());
			img.setPosition(vp.x + Shadow.vieww/2 - (0.75f*((1+hnum)-i)) + 0.1f, vp.y + 0.75f);
			img.draw(Shadow.spriteBatch, 0.9f);
		}
		*/

		if (player != null) {
			//NEW: Health bar 
			float alpha = 0.9f;
			
			float bgw = 5.1f;
			float fgw = player.health*5f;
			float bgh = 0.5f;
			float fgh = 0.4f;
			
			float xx1 = vp.x;
			float xx2 = xx1 + 0.05f;
			float yy1 = vp.y + bgh;
			float yy2 = yy1 - 0.05f;

			Image white = bgwhite;
			if (white == null) {
				white = Images.getImage("white");
			}
			
			white.setColor(0f, 0f, 0f, alpha);
			white.setPosition(xx1, yy1);
			white.setSize(1f, -1f);
			white.setScale(bgw, bgh);
			white.draw(Shadow.spriteBatch, 1f);
			
			white = fgwhite;
			if (white == null) {
				white = Images.getImage("white");
			}
			
			white.setColor(1f-1f*(player.health/player.MAXHEALTH), 1f*(player.health/player.MAXHEALTH), 0.2f, alpha);
			white.setPosition(xx2, yy2);
			white.setSize(1f, -1f);
			white.setScale(fgw, fgh);
			white.draw(Shadow.spriteBatch, 1f);
			
			if (pointblock == null) {
				fillLayer(0);
				pointblock = BlockType.getInstance("BlockPoint", 0, 0, layers.get(0));
			}
			
			float xx3 = vp.x;
			float xx4 = vp.x + 1.2f;
			float yy3 = vp.y + bgh - 0.1f;
			float yy4 = vp.y + bgh + 0.1f;

			pointblock.pos.set(xx3, yy3);
			pointblock.preRender();
			pointblock.render();

			font.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);
			
			font.draw(Shadow.spriteBatch, "x"+player.POINTS, xx4, yy4);
		}
		
	}
	
}
