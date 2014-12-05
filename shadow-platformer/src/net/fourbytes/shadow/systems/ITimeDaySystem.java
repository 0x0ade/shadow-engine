package net.fourbytes.shadow.systems;

/**
 * A TimeDaySystem manages the time of day in the given level, updating the LightSystem and others.
 * It also calls nextDay() on systems implementing INextDay.
 */
public interface ITimeDaySystem extends ISystem, ITickable {

    public float getTime();
    public int getDay();
    public float getDayDuration();
    public float getTimeNormalized();

    public void setTime(float time);
    public void setDay(int day);
    public void setDayDuration(float fullday);

}
