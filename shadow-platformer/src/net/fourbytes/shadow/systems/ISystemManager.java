package net.fourbytes.shadow.systems;

/**
 * An IServiceManager manages the systems of the given level.
 */
public interface ISystemManager extends ISystem, ITickable {
	public void init();
	public void init(String name);
	public void set(String name, ISystem system);

	public ISystem get(String name);
	public <T> T get(Class<T> isystem);
	public <T> T[] getAll(Class<T> clazz);
    public ISystem[] getAll();


}
