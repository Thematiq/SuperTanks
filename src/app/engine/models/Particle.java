package app.engine.models;

import app.engine.interfaces.IObserverDayChanged;
import app.engine.interfaces.ParticleType;
import app.engine.tools.Orientation;
import app.engine.tools.Vector;

/**
 * Class representing particle animation on a map
 * @author Mateusz Praski
 */
public class Particle extends AbstractMapObject implements IObserverDayChanged {
    public final ParticleType type;

    int lifespan;

    public Particle(ParticleType type, Vector pos) {
        this(type, pos, Orientation.NORTH);
    }

    public Particle(ParticleType type, Vector pos, Orientation orient) {
        super.position = pos;
        super.orient = orient;
        this.type = type;
        this.lifespan = this.type.getLifespan();
    }

    @Override
    public boolean newDay(int currentDay) {
        lifespan--;
        return lifespan <= 0;
    }

    public int getLifespan() { return this.lifespan; }
}
