package net.fourbytes.slimodk;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.android.AndroidController;
import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.ControllerHelper;
import net.fourbytes.shadow.Shadow;

import java.lang.reflect.Method;

/**
 * Contains alternative versions to the ODK's controller methods.
 * <br>
 * Part of SlimODK and thus needs SlimODK to be initialized for most of
 * it's parts to be functional.
 */
public final class SlimODKController {
	private SlimODKController() {
	}

	/**
	 * List of all LibGDX controllers registered by the OUYA.
	 */
	public final static Controller[] controllers = new Controller[4];
	static BroadcastReceiver receiver;

	/**
	 * Initializes the controller receiver for the current context if not already initialized.
	 * Requires the SlimODK itself to be initialized to access the current context.
	 */
	public static void init() {
		if (SlimODK.context == null || receiver != null) {
			return;
		}

		for (int i = 0; i < controllers.length; i++) {
			int deviceID = getIDForPlayer(i);
			if (deviceID != -1) {
				Controller controller = getControllerForID(deviceID);
				if (controller == null) {
					System.err.println("ControllerHelper didn't get PLAYER:"+i+";ID:"+deviceID+" yet! (init)");
				}
				controllers[i] = controller;
			}
		}

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("tv.ouya.controller.added")) {
					int playerNum = intent.getIntExtra("PLAYER_NUM", -1);
					if (playerNum < 0 || playerNum >= controllers.length) {
						return;
					}
					int deviceID = intent.getIntExtra("DEVICE_ID", -1);

					System.out.println("Adding controller with ID "+deviceID+" for player "+playerNum);

					if (controllers[playerNum] != null) {
						System.out.println("Already mapped OUYA controller to player #"+playerNum+"; remapping");
					}

					Controller controller = getControllerForID(deviceID);
					if (controller == null) {
						System.err.println("ControllerHelper didn't get PLAYER:"+playerNum+";ID:"+deviceID+" yet! (onReceive)");
					}
					controllers[playerNum] = controller;

				} else if (intent.getAction().equals("tv.ouya.controller.removed")) {
					int deviceID = intent.getIntExtra("DEVICE_ID", -1);

					System.out.println("Removing controller with ID "+deviceID);

					for (int i = 0; i < controllers.length; i++) {
						Controller controller = controllers[i];
						if (controller != null && getIDForController(controller) == deviceID) {
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
		SlimODK.context.registerReceiver(receiver, filter);
	}

	/**
	 * Unregisters the controller receiver for the current context if not already ended.
	 * Requires the SlimODK itself to be initialized to access the current context.
	 */
	public static void end() {
		if (SlimODK.context == null || receiver == null) {
			return;
		}

		for (int i = 0; i < controllers.length; i++) {
			controllers[i] = null;
		}

		SlimODK.context.unregisterReceiver(receiver);
		receiver = null;
	}

	/**
	 * Gets the controller with the given ID from the list of registered controllers.
	 * @param deviceID the controller's device ID
	 * @return the controller instance; null otherwise
	 */
	public static Controller getControllerForID(int deviceID) {
		ControllerHelper ch = Shadow.controllerHelper;
		if (ch == null) {
			return null;
		}

		for (Controller controller : ch.controllers) {
			if (getIDForController(controller) == deviceID) {
				return controller;
			}
		}

		return null;
	}

	/**
	 * Gets the device ID of the given controller.
	 * @param controller controller to get the ID from
	 * @return device ID in case the given controller is an instance of AndroidController; -1 otherwise
	 */
	public static int getIDForController(Controller controller) {
		return controller instanceof AndroidController ? ((AndroidController)controller).getDeviceId() : -1;
	}

	/**
	 * Gets the player's controller ID for the given player.
	 * @param playerNum the player to get the ID for
	 * @return the controller device ID; null otherwise
	 */
	public static int getIDForPlayer(int playerNum) {
		Uri controllerURI = Uri.parse("content://tv.ouya.controllerdata/");
		Cursor cursor = SlimODK.context.getContentResolver().query(controllerURI,
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
		int deviceID = cursor.getInt(0);
		cursor.close();
		return deviceID;
	}

	/**
	 * Gets the player number having the controller with the given ID.
	 * @param deviceID the player's controller ID
	 * @return the player's number; null otherwise
	 */
	public static int getPlayerForID(int deviceID) {
		Uri controllerURI = Uri.parse("content://tv.ouya.controllerdata/");
		Cursor cursor = SlimODK.context.getContentResolver().query(controllerURI,
				new String[] {"player_num"},
				"input_device_id = ?",
				new String[] {deviceID+""},
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


	private static String controllerMappingsStr;
	/**
	 * Returns the system's internal controller remapping as JsonValue.
	 * @return System's controller remapping as JsonValue; null if it failed (f.e. API < 11)
	 */
	public static JsonValue getControllerSystemMappings() {
		if (controllerMappingsStr != null) {
			return SlimODK.jsonReader.parse(controllerMappingsStr);
		}
		if (Build.VERSION.SDK_INT < 11) {
			return null;
		}

		ContentResolver cr = SlimODK.context.getApplicationContext().getContentResolver();

		Bundle bundle = null;
		try {
			//REFLECTION
			Method method = ContentResolver.class.getDeclaredMethod("call", Uri.class, String.class, String.class, Bundle.class);
			method.setAccessible(true);
			bundle = (Bundle) method.invoke(cr, Uri.parse("content://tv.ouya.controllerdata/"), "get_button_remap_json", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bundle == null) {
			return null;
		}

		controllerMappingsStr = bundle.getString("button_remap");

		if (controllerMappingsStr == null) {
			return null;
		}

		return SlimODK.jsonReader.parse(controllerMappingsStr);
	}

}
