package app.engine.interfaces;

import app.engine.models.AbstractMapObject;
import app.engine.tools.Vector;

/**
 * Position changed observer
 * @author Mateusz Praski
 */
public interface IObserverPositionChanged {
    void positionChanged(Vector oldPos, Vector newPos, AbstractMapObject caller);
}
