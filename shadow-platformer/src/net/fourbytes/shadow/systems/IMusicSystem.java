package net.fourbytes.shadow.systems;

/**
 * The MusicSystem manages the music in the given level that may change
 * as the game proceeds.
 */
public interface IMusicSystem extends ISystem, ITickable {

    public String getCurrent();
    public void setCurrent(String current);

}
