package net.fourbytes.shadow.systems;

/**
 * A System (...System, Default...System) processes and manages pre-defined logic. For example an LightSystem manages the
 * lights in the given level, a TimeDaySystem manages the time of day in the given level and
 * a ParticleManager manages the re-use of particle objects in the given level.
 */
public interface ISystem {
    public String getName();
}
