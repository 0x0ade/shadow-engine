package net.fourbytes.shadow.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * This class is an superclass of all {@link NetStream}s using KryoNet as underlying implementation. <br>
 * It contains various kryo-dependant helper methods.
 */
public abstract class NetKryoStream extends NetStream {

	public NetKryoStream() {
		super();
	}
	
	public void register(EndPoint ep) {
		Kryo kryo = ep.getKryo();
		kryo.setRegistrationRequired(false);
	}

}
