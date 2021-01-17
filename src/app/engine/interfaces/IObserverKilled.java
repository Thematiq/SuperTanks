package app.engine.interfaces;

import app.engine.models.AbstractMapObject;

/**
 * Killed observer
 * @author Mateusz Praski
 */
public interface IObserverKilled {
    void killed(AbstractMapObject caller);
}
