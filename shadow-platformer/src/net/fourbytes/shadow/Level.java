package net.fourbytes.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import net.fourbytes.shadow.blocks.BlockType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;

public class Level {
	
	public IntMap<Layer> layers = new IntMap<Layer>();
	public Layer mainLayer = new Layer(this);
	public LightEngine lights = new BasicLightEngine(this);
	public TimeDayEngine timeday = new TimeDayEngine(this);
	public Color globalLight = new Color(1f, 1f, 1f, 1f);
	public Player player;
	public Cursor c;
	public Array<Cursor> cursors = new Array<Cursor>();
	public float gravity = 0.02f;
	public float xgravity = 0.0f;
	
	public static int maxParticles = 512;
	
	//Abstract constructor, can be used for non-gameplay levels (menus).
	public Level() {
		ready = true;
	}
	
	public TiledMap map;
	public int tiledw = -1;
	public int tiledh = -1;
	public String nextlvl;
	
	public boolean hasvoid = true;
	public boolean ready = false;
	
	public Level(final String name) {
		try {
			TmxMapLoader tml = new TmxMapLoader();
			map = tml.load("data/levels/"+name+".tmx");
			initTilED();
		} catch (Throwable t) {
			t.printStackTrace();
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
	
	void initTilED() {
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
						GameObject obj = getGameObject(ln, x, -y, cell);
						if (obj instanceof Block) {
							ll.add((Block) obj);
						} else if (obj instanceof Entity) {
							ll.add((Entity) obj);
						}
					}
				}
			}
		}
		map.dispose();
	}
	
	GameObject getGameObject(int ln, int x, int y, Cell cell) {
		int tid = cell.getTile().getId();
		GameObject obj = null;
		String type = (String) cell.getTile().getProperties().get("type");
		String subtype = (String) cell.getTile().getProperties().get("subtype");
		if ("block".equals(type)) {
			//System.out.println("tid: "+tid);
			Block block = BlockType.getInstance(subtype, x, y, layers.get(ln));
			block.subtype = subtype;
			if ("false".equals((String)map.getLayers().get(ln).getProperties().get("solid"))) {
				block.solid = false;
			}
			obj = block;
		} else if ("entity".equals(type)) {
			if ("Player".equals(subtype)) {
				Entity ent = new Player(new Vector2(x, y), layers.get(ln));
				player = (Player) ent;
				obj = ent;
			}
		} else {
			if (tid != 0) {
				throw new IllegalStateException("unknown type "+type+" for block id "+tid);
			}
		}
		return obj;
	}
	
	boolean ftick = true;
	int tickid = 0;
	public void tick() {
		tickid++;
		if (tickid >= 20000) {
			tickid = 10000;
		}
		int nblocks = 0;
		int nentities = 0;
		//for (Layer ll : layers.values()) {
		Layer ll = mainLayer;
			//if (ll == null) continue;
			ll.cache = true;
			nblocks += ll.blocks.size;
			nentities += ll.entities.size;
			//TODO find perfect FPS
			//TODO decide
			//if (ll.blocks.size() <= 25000) {
			tickTiles(ll.blocks);
			/*} else {
				Rectangle vp = Shadow.cam.camrec;
				for (float y = vp.y-25f; y <= vp.y+vp.height+25f; y++) {
					for (float x = vp.x-25f; x <= vp.x+vp.width+25f; x++) {
						Coord c = new Coord(x, y);
						Vector<Block> blocks = ll.blockmap.get(c);
						if (blocks != null) {
							//blocks = (Vector<Block>) blocks.clone();
							tickTiles(blocks);
						}
					}
				}
			}*/
			
			int particle = 0;
			Array<Entity> entities = ll.entities;
			Array<Particle> particles = Garbage.particles;
			particles.clear();
			for (Entity entity : entities) {
				if (entity == null) continue;
				if (entity instanceof Particle) {
					if (!((Particle)entity).isStatic) {
						particles.add((Particle)entity);
						if (particle >= maxParticles) {
							ll.remove(particles.get(0));
							particles.removeIndex(0);
							continue;
						}
						particle++;
					}
				}
				entity.tick();
			}
			
			ll.cache = false;
			for (GameObject go : ll.addcache) {
				//go.tick();
				ll.add(go);
			}
			ll.addcache.clear();
			for (GameObject go : ll.remcache) {
				ll.remove(go);
			}
			ll.remcache.clear();
		//}
		lights.tick();
		timeday.tick();
		Garbage.cursors.clear();
		Garbage.cursors.addAll(cursors);
		for (Cursor c : Garbage.cursors) {
			c.tick();
		}
		c.tick();
		if (pointblock != null) {
			pointblock.tick();
		}
		ftick = false;
		
		//System.out.println("Blocks: "+nblocks+"; Entities: "+nentities);
	}
	
	Rectangle objrec = new Rectangle();
	
	protected void tickTiles(Array<Block> blocks) {
		for (Block block : blocks) {
			if (block == null) continue;
			objrec.set(block.pos.x-2f, block.pos.y-2f, block.rec.width+4f, block.rec.height+4f);
			if (Shadow.cam.camrec.overlaps(objrec) || block.interactive || ftick) {
				block.tick();
			}
		}
	}
	
	boolean canRenderImpl = true;
	static BitmapFont font = Fonts.light_normal;
	Image bgwhite;
	Image fgwhite;
	Block pointblock;
	
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
				white = new Image(Images.getTexture("white"));
			}
			white.setScaleY(-1f);
			
			white.setColor(0f, 0f, 0f, alpha);
			white.setPosition(xx1, yy1);
			white.setSize(bgw, bgh);
			white.draw(Shadow.spriteBatch, 1f);
			
			white = fgwhite;
			if (white == null) {
				white = new Image(Images.getTexture("white"));
			}
			white.setScaleY(-1f);
			
			white.setColor(1f-1f*(player.health/player.MAXHEALTH), 1f*(player.health/player.MAXHEALTH), 0.2f, alpha);
			white.setPosition(xx2, yy2);
			white.setSize(fgw, fgh);
			white.draw(Shadow.spriteBatch, 1f);
			
			if (pointblock == null) {
				fillLayer(0);
				pointblock = BlockType.getInstance("BlockPoint", 0, 0, layers.get(0));
			}
			
			float xx3 = vp.x;
			float xx4 = xx3 + 1.1f;
			float yy3 = vp.y + bgh - 0.4f ;
			float yy4 = yy3 + 0.5f;
			
			pointblock.pos.set(xx3, yy3);
			pointblock.preRender();
			pointblock.render();
			
			font.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);
			
			font.draw(Shadow.spriteBatch, "x"+player.POINTS, xx4, yy4);
		}
		
	}
	
}
