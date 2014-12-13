package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import net.fourbytes.shadow.blocks.BlockType;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;
import net.fourbytes.shadow.map.MapObject;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.systems.DefaultSystemManager;
import net.fourbytes.shadow.systems.ISystemManager;

import java.lang.reflect.Field;
import java.util.Iterator;

public class Level {

    public LongMap<GameObject> goIDMap = new LongMap<GameObject>();

	public ShadowMap map;

	public ISystemManager systems = createSystems();
	public IntMap<Layer> layers = new IntMap<Layer>(16);
	public Layer mainLayer = new Layer(this);
    @IsSaveable
	public Color globalLight = new Color(1f, 1f, 1f, 1f);
	public Player player;
	public Cursor c;
	public Array<Cursor> cursors = new Array<Cursor>(false, 8, Cursor.class);
    @IsSaveable
    public float gravity = 0.02f;
    @IsSaveable
    public float xgravity = 0.0f;

	public static float inviewf = 10f;

	//Abstract constructor, can be used for non-gameplay levels (menus).
	public Level() {
	}
	
	@IsSaveable
	public String nextlvl;

	public boolean ready = false;
	public boolean paused = false;
	public boolean ftick = true;
	public long tickid = 0;
	public boolean dirtify = false;

	public Level(String name) {
		this(name, null);
	}

	public Level(String name, LoadingLevel loading) {
		if (loading != null) {
			loading.bgpaused = true;
			loading.bglevel = this;
		}

		dirtify = false;

		fillLayer(0);
		c = new Cursor(new Vector2(0f, 0f), layers.get(0));

		if (Gdx.files.internal("data/levels/"+name+".smf").exists()) {
			map = ShadowMap.loadFile(Gdx.files.internal("data/levels/"+name+".smf"));
			map.fillLevel(this, loading);
		} else if (Gdx.files.internal("data/levels/"+name+".tmx").exists()) {
			map = new ShadowMap();
			try {
				TmxMapLoader tml = new TmxMapLoader();
				//TODO Custom TmxMapLoader that loads TiledMaps without loading tileset images
				initTilED(tml.load("data/levels/"+name+".tmx"), loading);
				ready = true;
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else {
			map = new ShadowMap();
			System.err.println("Map not found: "+name);
		}

		System.gc();

		dirtify = true;
		ready = true;
	}

	public ISystemManager createSystems() {
		ISystemManager systems = new DefaultSystemManager(this);
		systems.init();
		return systems;
	}

	public Layer fillLayer(int key) {
        Layer layer = layers.get(key);
		if (layer == null) {
            layer = new Layer(this);
			layers.put(key, layer);
		}
        return layer;
	}

	public void initTilED(TiledMap map) {
		initTilED(map);
	}

	public void initTilED(TiledMap map, LoadingLevel loading) {
		if (loading != null) {
			loading.progress = 0;
			loading.progressMax = 0;
		}
		map.dispose();
		//tiledw = map.width;
		//tiledh = map.height;
        int gridWidth = map.getProperties().get("tilewidth", Integer.class);
        int gridHeight = map.getProperties().get("tileheight", Integer.class);
		nextlvl = (String) map.getProperties().get("nextlvl");
        int pln = 0;
		for (int li = 0; li < map.getLayers().getCount(); li++) {
			MapLayer l = map.getLayers().get(li);
            int ln = li;
            if (l.getProperties().containsKey("layer")) {
                ln = pln + Integer.parseInt(l.getProperties().get("layer", String.class));
            }
            Layer ll = fillLayer(ln);
            if (l instanceof TiledMapTileLayer) {
                TiledMapTileLayer tl = (TiledMapTileLayer) l;
                for (int x = 0; x < tl.getWidth(); x++) {
                    for (int y = 0; y < tl.getHeight(); y++) {
                        Cell cell = tl.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            GameObject go = getGameObject(ln, x, tl.getHeight() - y, cell);
                            if (go instanceof Player) {
                                pln = ln;
                            }
                            ll.add(go);
                        }
                    }
                }
            }
            MapObjects objs = l.getObjects();
            if (objs != null) {
                for (int i = 0; i < objs.getCount(); i++) {
                    com.badlogic.gdx.maps.MapObject mo = objs.get(i);
                    GameObject go = getGameObject(ln,
                            (int) (mo.getProperties().get("x", Float.class) / gridWidth),
                            map.getProperties().get("height", Integer.class) - (int) (mo.getProperties().get("y", Float.class) / gridHeight), mo);
                    ll.add(go);
                }
            }
		}
	}
	
	protected GameObject getGameObject(int ln, int x, int y, Cell cell) {
		int tid = cell.getTile().getId();
		String type = cell.getTile().getProperties().get("type", String.class);
		String subtype = cell.getTile().getProperties().get("subtype", String.class);
		
		GameObject obj = ShadowMap.convert(x, y, layers.get(ln), tid, type, subtype);
		
		if (obj instanceof Player) {
			player = (Player) obj;
		}
		
		return obj;
	}

    protected GameObject getGameObject(int ln, int x, int y, com.badlogic.gdx.maps.MapObject mo) {
        String subtype = mo.getProperties().get("type", String.class);

        GameObject obj = ShadowMap.convert(x, y, layers.get(ln), -1, null, subtype);

        if (obj instanceof Player) {
            player = (Player) obj;
        }

        Class<? extends GameObject> clazz = obj.getClass();

        Iterator<String> keys = mo.getProperties().getKeys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            try {
                Field field = clazz.getField(key);
                field.setAccessible(true);
                Object value = mo.getProperties().get(key);
                Class type = field.getType();
                if (type.equals(byte.class)) {
                    field.setByte(obj, Byte.parseByte((String) value));
                } else if (type.equals(short.class)) {
                    field.setShort(obj, Short.parseShort((String) value));
                } else if (type.equals(int.class)) {
                    field.setInt(obj, Integer.parseInt((String) value));
                } else if (type.equals(long.class)) {
                    field.setLong(obj, Long.parseLong((String) value));
                } else if (type.equals(float.class)) {
                    field.setFloat(obj, Float.parseFloat((String) value));
                } else if (type.equals(double.class)) {
                    field.setDouble(obj, Double.parseDouble((String) value));
                } else if (type.equals(boolean.class)) {
                    field.setBoolean(obj, Boolean.parseBoolean((String) value));
                } else if (type.equals(char.class)) {
                    field.setChar(obj, ((String) value).charAt(0));
                } else {
                    field.set(obj, value);
                }
            } catch (Exception e) {
            }
        }

        return obj;
    }

	public void tick(float delta) {
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
				block.tick(delta);
			}

			block.frame(delta);
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
				entity.tick(delta);
			}

            entity.frame(delta);
		}

		for (int i = 0; i < mainLayer.particles.size; i++) {
			Particle particle = mainLayer.particles.items[i];
			if (particle == null) continue;

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

			particle.tick(delta);
		}

		if (ready) {
			systems.tick(delta);

			if (!paused) {
				if (Shadow.level == this && c != null) {
					for (int i = 0; i < cursors.size; i++) {
						Cursor c = cursors.items[i];
						c.tick(delta);
					}
					c.tick(delta);
					if (pointblock != null) {
						pointblock.tick(delta);
					}
				}
			}
		}

		ftick = false;
	}
	
	public boolean canRenderImpl = true;
	public BitmapFont font = Fonts.light_normal;
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
