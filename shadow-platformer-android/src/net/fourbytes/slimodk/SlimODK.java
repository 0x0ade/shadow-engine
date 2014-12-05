package net.fourbytes.slimodk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import com.badlogic.gdx.utils.JsonReader;

/**
 * This class is a trimmed down version containing trimmed down versions of
 * methods found in the official ODK, specially suited for LibGDX.
 * <br>
 * It doesn't contain all of the official ODK functionality yet.
 */
public final class SlimODK {
	private SlimODK() {
	}

	//GENERAL STUFF

	public final static String VERSION = "0.0.1";
	public final static int VERSION_INT = 0;

	static JsonReader jsonReader = new JsonReader();

	static Context context;
	static String devID;

	/**
	 * Simulates an initialization of the ODK and stores the context and
	 * developer ID for future usage. Automatically calls the init method of subsystems.
	 */
	public static void init(Context context, String devID) {
		if (SlimODK.context != null) {
			return;
		}

		SlimODK.context = context;
		SlimODK.devID = devID;

		Intent intent = new Intent("tv.ouya.ODK_INITIALIZED");
		intent.putExtra("package_name", context.getPackageName());
		context.sendBroadcast(intent);

		SlimODKIAP.init();
		SlimODKController.init();
	}

	/**
	 * Simulates a shutdown of the ODK. Automatically calls the end() method of subsystems.
	 */
	public static void end() {
		if (context == null) {
			return;
		}

		SlimODKIAP.end();
		SlimODKController.end();

		Intent intent = new Intent("tv.ouya.ODK_SHUTDOWN");
		intent.putExtra("package_name", context.getPackageName());
		context.sendBroadcast(intent);

		SlimODK.context = null;
		SlimODK.devID = null;
	}

	/**
	 * Returns whether this class is completely usable or the init method has not been called.
	 * @return true if class ready, false otherwise
	 */
	public static boolean isInitialized() {
		return context != null;
	}

	//DEVICE CODE

	private static int deviceGeneration = Integer.MIN_VALUE;
	/**
	 * Returns the current device identifier.
	 * @return OUYA reference device generation in case this device is an OUYA reference device;
	 * 0 in case this device is an OUYA Everywhere device; -1 in case this device responds to some of
	 * the OUYA ODK internal calls; -2 otherwise or if SlimODK not initialized.
	 */
	public static int getDeviceID() {
		if (deviceGeneration != Integer.MIN_VALUE) {
			return deviceGeneration;
		}
		if (context == null) {
			return -2;
		}

		//OUYA reference devices

		String device = Build.DEVICE;

		if ("ouya_1_1".equals(device) || "cardhu".equals(device)) {
			//cardhu may also be any random Tegra 3 device, such as the Nexus 7 (2012). Not tested, though.
			return deviceGeneration = 1;
		}
		//Add future reference generations as soon as they come out.


		//OUYA Everywhere devices and "fake" devices

		Intent info = context.registerReceiver(null, new IntentFilter("tv.ouya.DEVICE_INFO_ACTION"));

		if (info != null) {
			if (info.getBooleanExtra("SUPPORTED_DEVICE", false)) {
				return deviceGeneration = 0;
			} else {
				return deviceGeneration = -1;
			}
		}


		//Unsupported devices
		return deviceGeneration = -2;
	}

	private static String deviceName;
	/**
	 * Returns an user-friendly name of the current OUYA device if possible (OUYA Everywhere or OUYA reference device),
	 * or null in case this device does not respond to the ODK calls.
	 * <br>
	 * In case of unsupported devices that still respond to the device info calls, that response is returned. Due to
	 * such cases, it's advised to check whether the device is a supported OUYA Everywhere or OUYA reference device
	 * with the {@link #getDeviceID()} method.
	 * @return OUYA Everywhere / reference device name (f.e. "OUYA", "OUYA 2", "M.O.J.O."); null otherwise or if
	 * SlimODK not initialized
	 */
	public static String getDeviceName() {
		if (context == null || deviceName != null) {
			return deviceName;
		}

		int refgen = getDeviceID();
		if (refgen > 0) {
			return "OUYA"+(refgen>1?" "+refgen:"");
		}

		Intent info = context.registerReceiver(null, new IntentFilter("tv.ouya.DEVICE_INFO_ACTION"));

		if (info == null) {
			return null;
		}

		deviceName = info.getStringExtra("DEVICE_NAME");

		return deviceName;
	}

}
