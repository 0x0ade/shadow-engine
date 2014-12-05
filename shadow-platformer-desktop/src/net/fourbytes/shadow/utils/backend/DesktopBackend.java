package net.fourbytes.shadow.utils.backend;

import net.fourbytes.shadow.utils.PlayerInfo;

public class DesktopBackend extends Backend {

	@Override
	public void create() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public ModLoader newModLoader() {
		return new DesktopModLoader();
	}

	@Override
	public ControllerNumerator newControllerNumerator() {
		return new DefaultControllerNumerator();
	}

	@Override
	public PlayerInfo newPlayerInfo() {
		return new PlayerInfo();
	}
}
