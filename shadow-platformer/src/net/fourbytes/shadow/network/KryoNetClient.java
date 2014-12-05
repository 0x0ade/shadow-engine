package net.fourbytes.shadow.network;

import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.ModManager;

import java.io.IOException;

/**
 * This class is a client class for networking. It is using KryoNet as underlying implementation.
 */
public class KryoNetClient extends KryoNetStream {

    public static int bufferWrite = bufferObject*32;

	public Client client;
    public static int timeout = 10000;

    public KryoNetClient() {
		super();
		client = new Client(bufferWrite, bufferObject);
		register(client);
		client.addListener(new Listener() {
            @Override
            public void connected(Connection con) {
                KryoNetClient.this.connected(con);
				//TODO Mod support
			}

            @Override
			public void disconnected(Connection con) {
                KryoNetClient.this.disconnected(con);
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
                KryoNetClient.this.idle(con);
				//TODO
			}
		});
		client.start();
	}

    @Override
    public void handle(Data data, Object target) {
        if (ModManager.handleClient(data, target)) {
            return;
        }

        if (!(target instanceof Connection)) {
            return;
        }
        Connection c = (Connection) target;

        if (Shadow.level instanceof ClientLevel) {
            ((ClientLevel)Shadow.level).handle(data, c);
        }

        //TODO
    }

    @Override
	public void sendTCP(Data data, Object target) {
		if (target != null) {
			if (target instanceof Connection) {
				((Connection)target).sendTCP(data);
				return;
			}
		}
		client.sendTCP(data);
	}

	@Override
	public void sendUDP(Data data, Object target) {
		if (target != null) {
			if (target instanceof Connection) {
				((Connection)target).sendUDP(data);
				return;
			}
		}
		client.sendUDP(data);
	}

    @Override
    public void start() {
    }

    @Override
    public void connect(String ip) {
        int portTCP = NetStream.portTCP;
        int portUDP = NetStream.portUDP;

        int tcpIndex = ip.indexOf(":");
        if (tcpIndex > 0) {
            ip = ip.substring(0, tcpIndex);
            int udpIndex = ip.indexOf("/");
            if (udpIndex > 0) {
                portUDP = Integer.parseInt(ip.substring(udpIndex));
            } else {
                udpIndex = ip.length();
            }
            portTCP = Integer.parseInt(ip.substring(tcpIndex), udpIndex);
        }

        try {
            client.connect(timeout, ip, portTCP, portUDP);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        client.close();
    }

    @Override
    public void connected(Connection con) {
        client.sendTCP(new DataHandshake(Shadow.playerInfo));
    }

    @Override
    public void disconnected(Connection con) {
        //TODO
    }

    @Override
    public void idle(Connection con) {
    }

}
