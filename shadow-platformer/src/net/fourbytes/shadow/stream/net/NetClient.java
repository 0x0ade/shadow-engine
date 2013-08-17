package net.fourbytes.shadow.stream.net;

import java.io.IOException;

import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import net.fourbytes.shadow.stream.IStreamClient;

/**
 * This class is the standard client class for networking. It is using KryoNet as underlying implementation.
 */
public class NetClient extends NetStream implements IStreamClient {
	
	public Client client;
	
	public NetClient() {
		super();
		client = new Client(bufferWriteClient, bufferObject);
		client.addListener(new Listener() {
			public void connected(Connection con) {
				//TODO
			}
			
			public void disconnected(Connection con) {
				//TODO
			}
			
			public void received(Connection con, Object obj) {
				Entry entry = new Entry();
				entry.key = con;
				entry.value = obj;
				queueHandle.add(entry);
			}
			
			public void idle(Connection con) {
				//TODO
			}
		});
	}
	
	@Override
	public void handle(Object obj, Object target) {
		//TODO
		
	}

	@Override
	public void send0(Object o, Object target) {
		//TODO
		
	}
	
}
