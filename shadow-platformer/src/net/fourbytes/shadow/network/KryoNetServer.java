package net.fourbytes.shadow.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.ModManager;

import java.io.IOException;

/**
 * This class is a server class for networking. It is using KryoNet as underlying implementation.
 */
public class KryoNetServer extends KryoNetStream {

    public static int bufferWrite = bufferObject*512;

	public Server server;

	public ObjectMap<Connection, DataHandshake> connectionMap = new ObjectMap<Connection, DataHandshake>();
	public Array<Connection> connections = new Array<Connection>(Connection.class);
	public Array<DataHandshake> handshakes = new Array<DataHandshake>(DataHandshake.class);

	public Array<String> blacklistNames = new Array<String>(String.class);
	public Array<String> blacklistUUIDs = new Array<String>(String.class);
	public Array<String> clientSessionIDs = new Array<String>(String.class);

	public KryoNetServer() {
		super();
		server = new Server(bufferWrite, bufferObject);
		register(server);
		server.addListener(new Listener() {
			@Override
            public void connected(Connection con) {
				KryoNetServer.this.connected(con);
                //TODO Mod support
			}

            @Override
			public void disconnected(Connection con) {
                KryoNetServer.this.disconnected(con);
				//TODO Mod support
			}

            @Override
			public void received(Connection con, Object obj) {
                if (!(obj instanceof Data)) {
                    return;
                }
				Entry entry = entries.getNext();
				entry.key = con;
				entry.value = obj;
				queueHandle.add(entry);
				//handled on main thread in #handle(...)
			}

            @Override
			public void idle(Connection con) {
                KryoNetServer.this.idle(con);
				//TODO
			}
		});
	}
	
	@Override
	public void handle(Data data, Object target) {
		if (ModManager.handleServer(data, target)) {
			return;
		}

		if (!(target instanceof Connection)) {
			return;
		}
		Connection c = (Connection) target;

		if (data instanceof DataHandshake) {
			DataHandshake dh = (DataHandshake) data;

            if (!Shadow.gameID.equals(dh.gameID)) {
                System.out.println("S: "+dh.clientName+": KICK: version");
                c.sendTCP(new DataKick("Version mismatch."));
                c.close();
                return;
            }
			if (blacklistNames.contains(dh.clientName, false)) {
                System.out.println("S: "+dh.clientName+": KICK: blacklist (name)");
				c.sendTCP(new DataKick("Nickname on blacklist."));
				c.close();
				return;
			}
			if (blacklistUUIDs.contains(dh.clientUUID, false)) {
                System.out.println("S: "+dh.clientName+": KICK: blacklist (UUID)");
				c.sendTCP(new DataKick("UUID on blacklist."));
				c.close();
				return;
			}
			if (clientSessionIDs.contains(dh.clientSessionID, false)) {
                System.out.println("S: "+dh.clientName+": KICK: session");
				c.sendTCP(new DataKick("Session ID already used."));
				c.close();
				return;
			}

			connectionMap.put(c, dh);
			connections.add(c);
			handshakes.add(dh);
			clientSessionIDs.add(dh.clientSessionID);

			server.sendToAllTCP(new DataConnected(dh.clientName, dh.clientName+" joined."));

            if (Shadow.level instanceof ServerLevel) {
                ((ServerLevel)Shadow.level).send(c);
            }
		}

        if (Shadow.level instanceof ServerLevel) {
            ((ServerLevel)Shadow.level).handle(data, c);
        }

		//TODO more
	}

    @Override
	public void sendTCP(Data data, Object target) {
		if (target != null) {
			if (target instanceof Connection) {
				((Connection)target).sendTCP(data);
				return;
			}
		}
		server.sendToAllTCP(data);
	}

	@Override
	public void sendUDP(Data data, Object target) {
		if (target != null) {
			if (target instanceof Connection) {
				((Connection)target).sendUDP(data);
				return;
			}
		}
		server.sendToAllUDP(data);
	}

    @Override
    public void start() {
        try {
            server.bind(portTCP, portUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }

    @Override
    public void connect(String ip) {
    }

    @Override
    public void disconnect() {
        server.stop();
    }

    @Override
    public void connected(Connection con) {
        //as for now nothing, still awaiting handshake
    }

    @Override
    public void disconnected(Connection con) {
        DataHandshake dh = connectionMap.get(con);
        if (dh == null) {
            return;
        }
        connectionMap.remove(con);
        connections.removeValue(con, false);
        handshakes.removeValue(dh, false);
        clientSessionIDs.removeValue(dh.clientSessionID, false);
        server.sendToAllTCP(new DataDisconnected(dh.clientName, dh.clientName+" left."));
    }

    @Override
    public void idle(Connection con) {
    }
}
