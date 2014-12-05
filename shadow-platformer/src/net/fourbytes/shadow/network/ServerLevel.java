package net.fourbytes.shadow.network;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.LongMap;
import com.esotericsoftware.kryonet.Connection;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.map.DataChunk;
import net.fourbytes.shadow.map.MapObject;
import net.fourbytes.shadow.map.ShadowMap;
import net.fourbytes.shadow.systems.ISystem;
import net.fourbytes.shadow.systems.ITimeDaySystem;
import net.fourbytes.shadow.systems.IWeatherSystem;
import net.fourbytes.shadow.systems.SystemData;
import net.fourbytes.shadow.utils.gdx.IntLongMap;
import net.fourbytes.shadow.utils.gdx.LongIntMap;

public class ServerLevel extends Level {

    public Array<NetPlayer> players = new Array<NetPlayer>(NetPlayer.class);
    protected MapObject playerData = new MapObject();
    public LongIntMap playerCIDMap = new LongIntMap();
    public IntLongMap cidPlayerMap = new IntLongMap();

    public Array<String> usernames = new Array<String>(String.class);
    public LongMap<String> usernameMap = new LongMap<String>();

    public ServerLevel() {
        this("test");
    }

    public ServerLevel(String name) {
        super(name);

        systems.get(ITimeDaySystem.class).tick(0f);
        systems.get(ITimeDaySystem.class).setTime(0f);
        systems.get(IWeatherSystem.class).setWeather("SnowWeather");

        Shadow.server.start();
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);

        playerData.setOrdered(false); //UDP
        for (int i = 0; i < players.size; i++) {
            NetPlayer player = players.items[i];
            long id = player.getID();
            int cid = playerCIDMap.get(id, 0);
            ShadowMap.update(playerData, player);
            ((KryoNetServer)Shadow.server).server.sendToAllExceptUDP(cid, playerData);
        }

        ShadowMap.update(playerData, player);
        playerData.subtype = "NetPlayer";
        playerData.args.put("timestamp", System.currentTimeMillis());
        playerData.args.put("username", Shadow.playerInfo.getUserName());
        ((KryoNetServer)Shadow.server).server.sendToAllUDP(playerData);
    }

    public void handle(Data data, Connection c) {
        if (data instanceof DataHandshake) {
            DataHandshake dh = ((DataHandshake)data);
            usernames.add(dh.clientName);
            usernameMap.put(c.getID(), dh.clientName);
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
                    mo.args.put("username", usernameMap.get(cid));
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
            ((KryoNetServer)Shadow.server).server.sendToAllExceptTCP(c.getID(), dmu);
        }
    }

    public void send(Connection c) {
        map.createFrom(this);
        MapObject playerSpawned = null;
        for (DataChunk chunk : map.chunkmap.values()) {
            for (int i = 0; i < chunk.objects.size; i++) {
                MapObject mo = chunk.objects.items[i];
                if (mo.subtype.equals("Player") || mo.subtype.equals("NetPlayer")) {
                    if (playerSpawned != null) {
                        chunk.objects.removeIndex(i);
                        i--;
                        continue;
                    }
                    playerSpawned = mo;
                    Vector2 spawnpos = (Vector2) mo.args.get("spawnpos");
                    mo.x = spawnpos.x;
                    mo.y = spawnpos.y;
                } else if (mo.subtype.equals("PlayerSpawn")) {
                    if (playerSpawned != null) {
                        chunk.objects.removeValue(playerSpawned, true);
                        i--;
                    }
                    playerSpawned = mo;
                }
            }

            Shadow.server.send(chunk, c, false);
        }
        for (ISystem system : systems.getAll()) {
            SystemData data = new SystemData(system);
            Shadow.server.send(data, c, false);
        }
    }
}
