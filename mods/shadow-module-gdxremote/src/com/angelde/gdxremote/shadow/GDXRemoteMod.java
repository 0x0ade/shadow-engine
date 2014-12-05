package com.angelde.gdxremote.shadow;

import com.angelde.gdxremote.RemoteController;
import com.angelde.gdxremote.RemoteControllerListener;
import com.angelde.gdxremote.RemoteServer;
import com.angelde.gdxremote.Util;
import net.fourbytes.shadow.ParticleType;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.mod.AMod;

/**
 * The GDXRemoteMapping Module is a module / extension for Shadow Engine (/ games based on SE)
 * wrapping and housing a GDXRemoteMapping server. Running on separate threads,
 * working almost* as if usual controllers would connect and using LibGDX's
 * network classes, it's fit for use with Shadow Engine.
 * <br><br>
 * (* adds Shadow.controllerHelper as controller listener manually due to ControllerManager limitations)
 * @author maik
 */
public class GDXRemoteMod extends AMod implements RemoteControllerListener {
	public RemoteServer server;

	public GDXRemoteMod() {
		super();
	}

	@Override
	public String modName() {
		return "GDXRemote Wrapper";
	}

	@Override
	public String modAuthor() {
		return "AngelDE98";
	}

	@Override
	public String modVersion() {
		return "GDXRemoteMod: 0.0.0 beta 0; GDXRemoteMapping: "+ Util.VERSION_STR;
	}

	@Override
	public void create() {
		server = new RemoteServer();
		server.listeners.add(this);
		server.start();
	}

	@Override
	public void dispose() {
		if (server != null) {
			server.stop();
			server = null;
		}
	}

	@Override
	public void controllerConnected(RemoteController remoteController) {
		Shadow.controllerHelper.connected(remoteController);
		GDXRemoteMapping.map(Shadow.controllerHelper, remoteController);
	}

	@Override
	public void controllerDisconnected(RemoteController remoteController) {
		Shadow.controllerHelper.disconnected(remoteController);
	}

}
