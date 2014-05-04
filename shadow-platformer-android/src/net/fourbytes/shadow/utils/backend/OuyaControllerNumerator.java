package net.fourbytes.shadow.utils.backend;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.android.AndroidController;
import net.fourbytes.shadow.ControllerHelper;
import net.fourbytes.shadow.Shadow;

/**
 * This class is capable of doing stuff the ODK's
 * OuyaController class is able to do - attach
 * controllers to player IDs and device IDs.
 */
public class OuyaControllerNumerator extends ControllerNumerator {

	protected Context appContext;
	protected Controller[] controllers = new Controller[4];

	public OuyaControllerNumerator() {
		if (!Shadow.isOuya || appContext != null) {
			return;
		}

		appContext = ((Activity) Gdx.app).getApplicationContext();

		for (int i = 0; i < controllers.length; i++) {
			int deviceId = getIdForPlayer(i);
			if (deviceId != -1) {
				Controller controller = getControllerForId(deviceId);
				if (controller == null) {
					System.err.println("ControllerHelper didn't get PLAYER:"+i+";ID:"+deviceId+" yet! (init)");
				}
				controllers[i] = controller;
			}
		}

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("tv.ouya.controller.added")) {
					int playerNum = intent.getIntExtra("PLAYER_NUM", -1);
					if (playerNum < 0 || playerNum >= controllers.length) {
						return;
					}
					int deviceId = intent.getIntExtra("DEVICE_ID", -1);

					System.out.println("Adding controller with ID "+deviceId+" for player "+playerNum);

					if (controllers[playerNum] != null) {
						System.out.println("Already mapped Ouya controller to player #"+playerNum+"; remapping");
					}

					Controller controller = getControllerForId(deviceId);
					if (controller == null) {
						System.err.println("ControllerHelper didn't get PLAYER:"+playerNum+";ID:"+deviceId+" yet! (onReceive)");
					}
					controllers[playerNum] = controller;

				} else if (intent.getAction().equals("tv.ouya.controller.removed")) {
					int deviceId = intent.getIntExtra("DEVICE_ID", -1);

					System.out.println("Removing controller with ID "+deviceId);

					for (int i = 0; i < controllers.length; i++) {
						Controller controller = controllers[i];
						if (controller != null && getIdForController(controller) == deviceId) {
							controllers[i] = null;
							break;
						}
					}
				}
			}

		};

		IntentFilter filter = new IntentFilter();
		filter.addAction("tv.ouya.controller.added");
		filter.addAction("tv.ouya.controller.removed");
		appContext.registerReceiver(receiver, filter);
	}

	public Controller getControllerForId(int deviceId) {
		ControllerHelper ch = Shadow.controllerHelper;
		if (ch == null) {
			return null;
		}

		for (Controller controller : ch.controllers) {
			if (getIdForController(controller) == deviceId) {
				return controller;
			}
		}

		return null;
	}

	//Ouya specifiic getters

	public int getIdForController(Controller controller) {
		return controller instanceof AndroidController ?
				((AndroidController)controller).getDeviceId() : -1;
	}

	public int getIdForPlayer(int playerNum) {
		Uri controllerURI = Uri.parse("content://tv.ouya.controllerdata/");
		Cursor cursor = appContext.getContentResolver().query(controllerURI,
				new String[] {"input_device_id"},
				"player_num = ?",
				new String[] {playerNum+""},
				null);
		if (cursor == null) {
			return -1;
		}
		if (!cursor.moveToNext()) {
			cursor.close();
			return -1;
		}
		int deviceId = cursor.getInt(0);
		cursor.close();
		return deviceId;
	}

	public int getPlayerForId(int deviceId) {
		Uri controllerURI = Uri.parse("content://tv.ouya.controllerdata/");
		Cursor cursor = appContext.getContentResolver().query(controllerURI,
				new String[] {"player_num"},
				"input_device_id = ?",
				new String[] {deviceId+""},
				null);
		if (cursor == null) {
			return -1;
		}
		if (!cursor.moveToNext()) {
			cursor.close();
			return -1;
		}
		int playerNum = cursor.getInt(0);
		cursor.close();
		return playerNum;
	}

	//Overridden methods

	@Override
	public Controller[] getControllers() {
		return controllers;
	}

}
