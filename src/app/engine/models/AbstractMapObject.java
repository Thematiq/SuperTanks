package app.engine.models;

import app.engine.interfaces.IObserverKilled;
import app.engine.interfaces.IObserverPositionChanged;
import app.engine.tools.Orientation;
import app.engine.tools.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for map objects
 * @author Mateusz Praski
 */
public abstract class AbstractMapObject {
    private final List<IObserverPositionChanged> observersPositionChanged = new ArrayList<>();
    private final List<IObserverKilled> observersKilled = new ArrayList<>();
    protected int HP;
    private boolean killed = true;

    Vector position = new Vector(0, 0);
    Orientation orient = Orientation.NORTH;

    public Vector getPosition() { return this.position; }

    public Orientation getOrient() { return this.orient; }

    public void addPositionObserver(IObserverPositionChanged obs) {
        this.observersPositionChanged.add(obs);
    }

    protected void callObservers(Vector oldPos, Vector newPos) {
        for (IObserverPositionChanged o : this.observersPositionChanged) {
            o.positionChanged(oldPos, newPos, this);
        }
    }

    public void addKiller(IObserverKilled o) {
        this.observersKilled.add(o);
    }

    public void kill() {
        this.killed = true;
        for(IObserverKilled o : this.observersKilled) {
            o.killed(this);
        }
    }

    public boolean isDead() { return this.killed; }
}
