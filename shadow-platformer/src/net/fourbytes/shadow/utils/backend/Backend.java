package net.fourbytes.shadow.utils.backend;

import com.badlogic.gdx.audio.Music;
import net.fourbytes.shadow.utils.PlayerInfo;

public abstract class Backend {

	public abstract void create();
	public abstract void dispose();

	public abstract ModLoader newModLoader();
	public abstract ControllerNumerator newControllerNumerator();

	public abstract PlayerInfo newPlayerInfo();

}
