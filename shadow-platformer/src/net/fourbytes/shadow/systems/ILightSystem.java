package net.fourbytes.shadow.systems;

/**
 * The LightSystem manages the light in the given level, for example ambient light which changes
 * with the daytime or light cast by objects.
 */
public interface ILightSystem extends ISystem {
	public void render();
	public void renderFBO();
}
