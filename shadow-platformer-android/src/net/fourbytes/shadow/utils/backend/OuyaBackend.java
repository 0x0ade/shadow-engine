package net.fourbytes.shadow.utils.backend;

import android.app.Activity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.backends.android.AndroidInput;
import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.Camera;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.PlayerInfo;
import net.fourbytes.slimodk.SlimODKController;
import net.fourbytes.slimodk.SlimODKIAP;

public class OuyaBackend extends AndroidBackend {
	public OuyaBackend(AndroidApplicationConfiguration cfgApp) {
		super(cfgApp);
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new OuyaControllerNumerator();
	}

	@Override
	public void create() {
		//This method doesn't call the Android backend's method!

		//I guess the OUYA is powerful for enough particles...
		/*
		GameObject.pixffac = 2;
		Level.maxParticles = 128;
		 */
		Camera.blursize = 4f;

		//OUYA controller mapping

		JsonValue mappings = SlimODKController.getControllerSystemMappings();
		if (mappings.has("controller")) {
			JsonValue controllers = mappings.get("controller");
			for (JsonValue controller = controllers.child; controller != null; controller = controller.next) {
				OuyaSystemControllerMapping mapping = new OuyaSystemControllerMapping();

				//Selfnote: Blame OUYA for adding nameless values in their JSON, which crashes LibGDX's JsonValue...

				JsonValue aliases = Garbage.get(controller, "alias");
				for (JsonValue alias = aliases.child; alias != null; alias = alias.next) {
					//fallback is useless as Shadow Engine handles controller fallbacks via ControllerNumerator
					//boolean fallback = alias.getBoolean("fallback", false);

					//name doesn't equal the real controller name...
					mapping.names.add(Garbage.get(alias, "name").asString());

					//nameFriendly is null in some cases
					JsonValue nameFriendly = Garbage.get(alias, "nameFriendly");
					if (nameFriendly != null) {
						mapping.names.add(nameFriendly.asString());
					}
				}

				//unclear how axis_exclude_source should be useful
				/*
				if (controller.has("axis_exclude_source")) {
					JsonValue excludedSources = controller.get("axis_exclude_source");
					for (int ei = 0; ei < excludedSources.size; ei++) {
						int excludedSource = excludedSources.getInt(ei);
						//in case axis_exclude_source shall be usable, add source to list of excluded sources
					}
				}
				*/

				JsonValue axes = controller.get("axis_remap");
				if (axes != null && !axes.isNull()) {
					for (JsonValue axis = axes.child; axis != null; axis = axis.next) {
						mapping.axes.put(axis.getInt("destination_axis"), axis.getInt("source_axis"));
					}
				}

				//button_is_axis is useless as Shadow Engine uses axes as buttons automatically when needed
				/*
				if (controller.has("button_is_axis")) {
					JsonValue buttons = controller.get("button_is_axis");
					for (int bi = 0; bi < buttons.size; bi++) {
						JsonValue button = buttons.get(bi);
						int source = button.getInt("source_axis");
						float downMin = (float) button.getDouble("action_down_min");
						float downMax = (float) button.getDouble("action_down_max");
						int dest = button.getInt("destination_keycode");
						//in case button_is_axis shall be usable, store somewhere
					}
				}
				*/

				JsonValue buttons = controller.get("button");
				if (buttons != null && !buttons.isNull()) {
					for (JsonValue button = buttons.child; button != null; button = button.next) {
						//unclear how exclude_source should be useful
						/*
						if (button.has("exclude_source")) {
							JsonValue excludedSources = button.get("exclude_source");
							for (int ei = 0; ei < excludedSources.size; ei++) {
								int excludedSource = excludedSources.getInt(ei);
								//in case exclude_source shall be usable, add source to list of excluded sources
							}
						}
						*/
						mapping.buttons.put(button.getInt("destination_keycode"), button.getInt("source_keycode"));
					}
				}

				Shadow.controllerHelper.mappings.add(mapping);
			}
		}
	}

	@Override
	public PlayerInfo newPlayerInfo() {
		JsonValue gamerData = SlimODKIAP.getGamerData();

		if (gamerData == null) {
			//fallback
			return super.newPlayerInfo();
		}

		JsonValue gamer = gamerData.get("gamer");
		String userName = gamer.getString("username");
		String userID = gamer.getString("uuid");

		return new OuyaPlayerInfo(userName, userID, "");
	}

	@Override
	public AndroidInput getGdxInput() {
		return new OuyaInput(Gdx.app, ((Activity)Gdx.app), ((AndroidGraphics)Gdx.graphics).getView(), cfgApp);
	}

}
