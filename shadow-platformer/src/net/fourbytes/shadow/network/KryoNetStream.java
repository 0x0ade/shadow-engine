package net.fourbytes.shadow.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * This class is a networking class extending NetStream. <br>
 * It's using KryoNet as underlying implementation.
 */
public abstract class KryoNetStream extends NetStream {

    public static int bufferObject = 32768;

    /**
     * Registers the EndPoint (Server, Client). Should be called after creating it.
     * @param ep EndPoint / Server / Client / ... to register
     */
    public void register(EndPoint ep) {
        Kryo kryo = ep.getKryo();
        kryo.setRegistrationRequired(false);
    }

    public abstract void connected(Connection con);
    public abstract void disconnected(Connection con);
    public abstract void idle(Connection con);
}
