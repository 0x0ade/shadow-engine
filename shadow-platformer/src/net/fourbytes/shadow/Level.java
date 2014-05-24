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
import com.badlogic.gdx.utils.IntMap;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Particle;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.Saveable;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.mod.ModManager;

public class Level {
	
	public IntMap<Layer> layers = new IntMap<Layer>(16);
	public Layer mainLayer = new Layer(this);
	public LightSystem lights;
	public TimeDaySystem timeday;
	public Color globalLight = new Color(1f, 1f, 1f, 1f);
	public Player player;
	public Cursor c;
	public Array<Cursor> cursors = new Array<Cursor>(false, 8, Cursor.class);
	@Saveable
	public float gravity = 0.02f;
	@Saveable
	public float xgravity = 0.0f;
	
	public static int maxParticles = 512;
	public static float inviewf = 10f;

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

		initSystems();

		System.gc();

		ready = true;
	}

	public void initSystems() {
		ModManager.initLevelSystems(this);

		if (lights == null) {
			lights = new LightSystem(this);
		}

		if (timeday == null) {
			timeday = new TimeDaySystem(this);
		}
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
			for (int x = 0; x < l.getWidth(); x++) {
				for (int y = 0; y < l.getHeight(); y++) {
					Cell cell = l.getCell(x, y);
					if (cell != null && cell.getTile() != null) {
						GameObject obj = getGameObject(ln, x, l.getHeight()-y, cell);
						ll.add(obj);
					}
				}
			}
		}
	}
	
	protected GameObject getGameObject(int ln, int x, int y, Cell cell) {
		int tid = cell.getTile().getId();
		String type = (String) cell.getTile().getProperties().get("type");
		String subtype = (String) cell.getTile().getProperties().get("subtype");
		
		GameObject obj = ShadowMap.convert(x, y, layers.get(ln), tid, type, subtype);
		
		if (obj instanceof Player) {
			player = (Player) obj;
		}
		
		return obj;
	}

	public void tick() {
		Rectangle vp = Shadow.cam.camrec;

		tickid++;

		mainLayer.inView.clear();
		for (Layer ll : layers.values()) {
			if (ll == null) continue;
			ll.inView.clear();
		}

		float ox, oy, ow, oh;

		boolean shallTick;
		for (int i = 0; i < mainLayer.blocks.size; i++) {
			Block block = mainLayer.blocks.items[i];
			if (block == null) continue;
			ox = block.pos.x-inviewf;
			oy = block.pos.y-inviewf;
			ow = block.rec.width+inviewf*2f;
			oh = block.rec.height+inviewf*2f;

			if (vp.x < ox + ow && vp.x + vp.width > ox && vp.y < oy + oh && vp.y + vp.height > oy) {
				Array<GameObject> inView;

				inView = mainLayer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size / 2);
				}
				inView.items[inView.size++] = block;

				inView = block.layer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size/2);
				}
				inView.items[inView.size++] = block;
				
				shallTick = block.tickInView;
			} else {
				shallTick = false;
			}

			if ((ftick || block.tickAlways || shallTick) && !paused) {
				block.tick();
			}
		}
		
		for (int i = 0; i < mainLayer.entities.size; i++) {
			Entity entity = mainLayer.entities.items[i];
			if (entity == null) continue;

			ox = entity.pos.x-inviewf;
			oy = entity.pos.y-inviewf;
			ow = entity.rec.width+inviewf*2f;
			oh = entity.rec.height+inviewf*2f;

			if (vp.x < ox + ow && vp.x + vp.width > ox && vp.y < oy + oh && vp.y + vp.height > oy) {
				Array<GameObject> inView;

				inView = mainLayer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size / 2);
				}
				inView.items[inView.size++] = entity;

				inView = entity.layer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size/2);
				}
				inView.items[inView.size++] = entity;
			}

			if (!paused) {
				entity.tick();
			}
		}

		boolean tickPaused;
		int particleCount = 0;
		for (int i = 0; i < mainLayer.particles.size; i++) {
			Particle particle = mainLayer.particles.items[i];
			if (particle == null) continue;
			if (!particle.interactive) {
				if (particleCount >= maxParticles) {
					mainLayer.remove(mainLayer.particles.items[0]);
					continue;
				}
				particleCount++;
				tickPaused = true;
			} else {
				tickPaused = false;
			}


			ox = particle.pos.x-inviewf;
			oy = particle.pos.y-inviewf;
			ow = particle.rec.width+inviewf*2f;
			oh = particle.rec.height+inviewf*2f;

			if (vp.x < ox + ow && vp.x + vp.width > ox && vp.y < oy + oh && vp.y + vp.height > oy) {
				Array<GameObject> inView;

				inView = mainLayer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size / 2);
				}
				inView.items[inView.size++] = particle;

				inView = particle.layer.inView;
				if (inView.size == inView.items.length) {
					inView.items = inView.ensureCapacity(inView.size/2);
				}
				inView.items[inView.size++] = particle;
			}

			if (tickPaused || !paused) {
				particle.tick();
			}
		}

		if (!paused) {
			if (timeday != null) {
				timeday.tick();
			}

			if (Shadow.level == this && c != null) {
				for (int i = 0; i < cursors.size; i++) {
					Cursor c = cursors.items[i];
					c.tick();
				}
				c.tick();
				if (pointblock != null) {
					pointblock.tick();
				}
			}
		}

		ftick = false;
	}
	
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
		
		if (player != null) {
			float alpha = 0.9f;
			
			float bgw = 5.1f;
			float fgw = player.health*5f;
			float bgh = 0.5f;
			float fgh = 0.4f;
			
			float xx1 = vp.x;
			float xx2 = xx1 + 0.05f;
			float yy1 = vp.y + bgh;
			float yy2 = yy1 - 0.05f;

			Image white;

			if (bgwhite == null) {
				bgwhite = Images.getImage("white");
			}
			white = bgwhite;

			white.setColor(0f, 0f, 0f, alpha);
			white.setPosition(xx1, yy1);
			white.setSize(1f, -1f);
			white.setScale(bgw, bgh);
			white.draw(Shadow.spriteBatch, 1f);

			if (fgwhite == null) {
				fgwhite = Images.getImage("white");
			}
			white = fgwhite;

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
			
			font.draw(Shadow.spriteBatch, "x"+player.points, xx4, yy4);
		}
		
	}
	
}
