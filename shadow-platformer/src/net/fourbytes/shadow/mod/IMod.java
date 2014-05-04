package net.fourbytes.shadow.mod;

/**
 * IMod (short for InterfaceMod) contains all the methods
 * a mod (module) <b>must override by default</b> to make f.e.
 * getting mod information (name, author, version) possible and
 * to create and dispose module resources when needed.
 */
public interface IMod {

	public String modName();
	public String modAuthor();
	public String modVersion();

	public void create();
	public void dispose();

}
