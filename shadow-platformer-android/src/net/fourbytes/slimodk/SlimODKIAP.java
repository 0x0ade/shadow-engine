package net.fourbytes.slimodk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Contains alternative versions to the ODK's IAP methods.
 * <br>
 * Part of SlimODK and thus needs SlimODK to be initialized for most of
 * it's parts to be functional.
 */
public final class SlimODKIAP {
	private SlimODKIAP() {
	}

	static ServiceConnection connection;
	static IBinder service;

	/**
	 * Initializes the IAP service binding to the current context if not already initialized.
	 * Requires the SlimODK itself to be initialized to access the current context.
	 */
	public static void init() {
		if (SlimODK.context == null || connection != null) {
			return;
		}

		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				SlimODKIAP.service = service;
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				SlimODKIAP.service = null;
			}
		};

		Intent intent = new Intent();
		intent.setClassName("tv.ouya", "tv.ouya.console.service.iap.IapService");
		boolean success = SlimODK.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

		if (!success) {
			System.out.println("iapService binding failed!");
			connection = null;
		}
	}

	/**
	 * Ends the IAP service binding to the current context if not already ended.
	 * Requires the SlimODK itself to be initialized to access the current context.
	 */
	public static void end() {
		if (SlimODK.context == null || connection == null) {
			return;
		}

		SlimODK.context.unbindService(connection);
		connection = null;
	}

	private static String gamerDataStr;
	/**
	 * Returns the data of the current user ("gamer") as JsonValue via the IAP API.
	 * It's a relatively long blocking call, caching only a string representing the JsonValue, so use wisely!
	 * @return JsonValue representing current gamer; null if an error happened or if IAP not initialized
	 */
	public static JsonValue getGamerData() {
		if (gamerDataStr != null) {
			SlimODK.jsonReader.parse(gamerDataStr);
		}
		if (SlimODK.context == null || connection == null) {
			return null;
		}

		//IBinder that gets the response data
		IBinder binder = new Binder() {
			@Override
			public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
				//We only need to handle when transaction was a success.
				if (code == 1) {
					//The OUYA enforces an IStringListener as binder
					data.enforceInterface("tv.ouya.console.internal.IStringListener");

					//Read the data, check for being null, exception and return true
					gamerDataStr = data.readString();
					if (gamerDataStr == null) {
						gamerDataStr = "";
					}
					reply.writeNoException();
					return true;
				}

				//Otherwise, it just simply failed
				gamerDataStr = "";
				return super.onTransact(code, data, reply, flags);
			}
		};

		Parcel data = Parcel.obtain();
		Parcel reply = Parcel.obtain();

		try {
			//The OUYA enforces an IIapServiceDefinition to send the transaction
			data.writeInterfaceToken("tv.ouya.console.internal.IIapServiceDefinition");
			//Developer-specific devID
			data.writeString(SlimODK.devID);
			//Set the binder that listens to the response
			data.writeStrongBinder(binder);

			//Transact the data with the iapService. The reply will be empty.
			service.transact(7, data, reply, 0);
			//Check for exceptions
			reply.readException();
		} catch (RemoteException e) {
			e.printStackTrace();
			gamerDataStr = null;
			return null;
		} finally {
			reply.recycle();
			data.recycle();
		}

		//Wait for the gamerDataStr to be non-null as the transaction happens asynchronously.
		while (gamerDataStr == null) {
			Thread.yield();
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (gamerDataStr.isEmpty()) {
			gamerDataStr = null;
			return null;
		}

		JsonValue json = SlimODK.jsonReader.parse(gamerDataStr);
		return json;
	}

}
