package app.engine.interfaces;

public interface IObserverDayChanged {
    boolean newDay(int currentDay);
    void kill();
}
