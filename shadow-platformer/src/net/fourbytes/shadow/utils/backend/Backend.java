package net.fourbytes.shadow.utils.backend;

public abstract class Backend {

	public abstract void create();

	public abstract ModLoader newModLoader();
	public abstract ControllerNumerator newControllerNumerator();

}
