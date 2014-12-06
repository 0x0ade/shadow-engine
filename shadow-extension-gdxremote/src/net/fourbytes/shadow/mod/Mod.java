package net.fourbytes.shadow.mod;

import com.angelde.gdxremote.RemoteController;
import com.angelde.gdxremote.RemoteControllerListener;
import com.angelde.gdxremote.RemoteServer;
import com.angelde.gdxremote.Util;
import net.fourbytes.shadow.Shadow;

/**
 * The GDXRemote Module is a module / extension for Shadow Engine (/ games based on SE)
 * wrapping and housing a GDXRemote server. Running on separate threads,
 * working almost* as if usual controllers would connect and using LibGDX's
 * network classes, it's fit for use with Shadow Engine.
 * <br><br>
 * (* except on Android due to the nature of LibGDX's Android ControllerManager)
 * @author maik
 */
public class Mod extends AMod implements RemoteControllerListener {
	public RemoteServer server;

	public Mod(ModContainer cont) {
		super(cont);
	}

	@Override
	public String modName() {
		return "GDXRemote Wrapper for Shadow Engine";
	}

	@Override
	public String modAutor() {
		return "AngelDE98";
	}

	@Override
	public String modVersion() {
		return "Mod: 0.0.0 beta 0; GDXRemote: "+ Util.VERSION_STR;
	}

	@Override
	public void loadResources() {
		server = new RemoteServer();
		server.listeners.add(this);
		server.start();
	};

	@Override
	public void disposeResources() {
		server.stop();
	};

	@Override
	public void controllerConnected(RemoteController remoteController) {
		if (Shadow.isAndroid) {
			remoteController.addListener(Shadow.controllerHelper);
		}
	}

	@Override
	public void controllerDisconnected(RemoteController remoteController) {
		if (Shadow.isAndroid) {
			remoteController.removeListener(Shadow.controllerHelper);
		}
	}
}
