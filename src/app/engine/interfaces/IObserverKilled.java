package app.engine.interfaces;

import app.engine.models.AbstractMapObject;

public interface IObserverKilled {
    void killed(AbstractMapObject caller);
}
