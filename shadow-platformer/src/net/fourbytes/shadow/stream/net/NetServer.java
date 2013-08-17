package net.fourbytes.shadow.stream.net;

import java.io.IOException;

import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import net.fourbytes.shadow.stream.IStreamServer;

/**
 * This class is the standard server class for networking. It is using KryoNet as underlying implementation.
 */
public class NetServer extends NetStream implements IStreamServer {
	
	public Server server;
	
	public NetServer() {
		super();
		server = new Server(bufferWriteServer, bufferObject);
		server.addListener(new Listener() {
			public void connected(Connection con) {
				//TODO
			}
			
			public void disconnected(Connection con) {
				//TODO
			}
			
			public void received(Connection con, Object obj) {
				//TODO
				Entry entry = new Entry();
				entry.key = con;
				entry.value = obj;
				queueHandle.add(entry);
			}
			
			public void idle(Connection con) {
				//TODO
			}
		});
		try {
			server.bind(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.start();
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
