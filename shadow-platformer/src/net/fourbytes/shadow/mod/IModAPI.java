package net.fourbytes.shadow.mod;

/**
 * A IModAPI is a collection of methods to be called by
 * either the Shadow Engine itself or other mods. Mods
 * can create their own ModAPIs to be called by other
 * mods when needed or wanted. Due to that, all ModAPIs
 * should be managed by the ModManager.
 */
public interface IModAPI {

	public String apiName();
	public String apiAuthor();
	public String apiVersion();

	public IMod getModule();

}
