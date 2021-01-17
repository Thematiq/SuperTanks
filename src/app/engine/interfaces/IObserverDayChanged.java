package app.engine.interfaces;

/**
 * New day observer
 * @author Mateusz Praski
 */
public interface IObserverDayChanged {
    boolean newDay(int currentDay);
    void kill();
}
