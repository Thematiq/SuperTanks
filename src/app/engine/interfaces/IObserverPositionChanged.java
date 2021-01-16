package app.engine.interfaces;

import app.engine.models.AbstractMapObject;
import app.engine.tools.Vector;

public interface IObserverPositionChanged {
    void positionChanged(Vector oldPos, Vector newPos, AbstractMapObject caller);
}
