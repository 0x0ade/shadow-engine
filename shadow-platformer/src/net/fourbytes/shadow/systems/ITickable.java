package net.fourbytes.shadow.systems;

/**
 * ITickable is an interface to easily enforce the tick() method on children classes / interfaces.
 * It also is a typing interface.
 */
public interface ITickable {
	public void tick(float tick);
}
