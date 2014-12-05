package net.fourbytes.shadow.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Connection;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Cursor;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.DataChunk;
import net.fourbytes.shadow.map.MapObject;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.systems.ISystem;
import net.fourbytes.shadow.systems.SystemData;
import net.fourbytes.shadow.utils.gdx.IntLongMap;
import net.fourbytes.shadow.utils.gdx.LongIntMap;

public class ClientLevel extends Level {

    public String ip;

    public Array<NetPlayer> players = new Array<NetPlayer>(NetPlayer.class);
    protected MapObject playerData = new MapObject();
    public LongIntMap playerCIDMap = new LongIntMap();
    public IntLongMap cidPlayerMap = new IntLongMap();

    public ClientLevel(String ip) {
        this.ip = ip;

        Shadow.client.disconnect();
        Shadow.client.connect(ip);

        fillLayer(0);
        player = new Player(new Vector2(0f, 0f), layers.get(0)); //Dummy player for proper system initialization
        c = new Cursor(new Vector2(0f, 0f), layers.get(0));

        System.gc();

        ready = true;
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        playerData.setOrdered(false); //UDP
        ShadowMap.update(playerData, player);
        playerData.subtype = "NetPlayer";
        playerData.args.put("timestamp", System.currentTimeMillis());
        Shadow.client.send(playerData);
    }

    public void handle(Data data, Connection c) {
        if (data instanceof ShadowMap) {
            map = ((ShadowMap)data);
            while (mainLayer.blocks.size > 0) {
                mainLayer.blocks.items[0].layer.remove(mainLayer.blocks.items[0]);
            }
            while (mainLayer.entities.size > 0) {
                mainLayer.entities.items[0].layer.remove(mainLayer.entities.items[0]);
            }

            map.fillLevel(this);
        }

        if (data instanceof DataChunk) {
            DataChunk chunk = ((DataChunk)data);
            for (int i = 0; i < chunk.objects.size; i++) {
                MapObject mo = chunk.objects.items[i];
                if (mo.subtype.equals("Player")) {
                    mo.id = Shadow.rand.nextLong();
                }
            }
            if (map == null) {
                map = new ShadowMap();
            }
            map.chunkmap.put(Coord.get(chunk.x, chunk.y), chunk);
            map.convert(chunk, this, true);
        }

        if (data instanceof MapObject) {
            MapObject mo = ((MapObject)data);
            if (mo.subtype.equals("NetPlayer")) {
                int cid = c.getID();
                long id = mo.id;
                GameObject go = goIDMap.get(id);
                if (go == null) {
                    go = ShadowMap.convert(mo, this);
                    go.layer = player.layer;
                    go.layer.add(go);
                    players.add((NetPlayer) go);
                    playerCIDMap.put(id, cid);
                    cidPlayerMap.put(cid, id);
                } else if (go instanceof NetPlayer &&
                        ((NetPlayer)go).timestamp < ((Long) mo.args.get("timestamp"))) {
                    ShadowMap.update(go, mo);
                }
            }
        }

        if (data instanceof DataMapUpdate) {
            DataMapUpdate dmu = ((DataMapUpdate)data);
            if (dmu.mode == DataMapUpdate.MapUpdateModes.ADD) {
                GameObject go = ShadowMap.convert(dmu.object, this);
                go.layer.add(go);
            } else if (dmu.mode == DataMapUpdate.MapUpdateModes.REMOVE) {
                Layer layer = layers.get(dmu.object.layer);
                if (dmu.object.type == null) {
                    Array<Block> blocks = layer.get(Coord.get(dmu.object.x, dmu.object.y));
                    if (blocks != null) {
                        for (int i = 0; i < blocks.size; i++) {
                            layer.remove(blocks.items[i]);
                        }
                    }
                } else {
                    GameObject go = goIDMap.get(dmu.object.id);
                    if (go != null) {
                        go.layer.remove(go);
                    }
                }
            } else if (dmu.mode == DataMapUpdate.MapUpdateModes.UPDATE) {
                GameObject go = goIDMap.get(dmu.object.id);
                if (go != null) {
                    ShadowMap.update(go, dmu.object);
                }
            }
        }

        if (data instanceof SystemData) {
            SystemData systemData = ((SystemData)data);
            ISystem system = systems.get(systemData.systemName);
            systemData.set(system);
        }
    }


}
