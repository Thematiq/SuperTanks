package app.engine.controllers;

import app.engine.interfaces.IObserverKilled;
import app.engine.interfaces.IObserverPositionChanged;
import app.engine.models.AbstractMapObject;
import app.engine.models.Bullet;
import app.engine.models.Particle;
import app.engine.models.Schutzengrabenvernichtungspanzerkraftwagen;
import app.engine.tools.Vector;

import java.util.*;

public class WorldMap implements IObserverPositionChanged, IObserverKilled {
    // Fun fact
    // You can't import class named Tank
    // So I had to do small refactoring
    private final Map<Vector, AbstractMapObject> instances = new HashMap<>();
    private final Map<Vector, Particle> particleMap = new HashMap<>();
    private final List<Bullet> bulletHell = new ArrayList<>();
    private final List<Schutzengrabenvernichtungspanzerkraftwagen> enemyTanks = new ArrayList<>();
    private final Schutzengrabenvernichtungspanzerkraftwagen playerTank;

    public WorldMap(int playerHP) {
        this.playerTank = new Schutzengrabenvernichtungspanzerkraftwagen();
        this.playerTank.addPositionObserver(this);
        this.playerTank.setHP(playerHP);
    }

    public Schutzengrabenvernichtungspanzerkraftwagen getPlayerTank() { return this.playerTank; }

    public List<Schutzengrabenvernichtungspanzerkraftwagen> getEnemyTanks() {return Collections.unmodifiableList(this.enemyTanks); }

    public List<Bullet> getBulletHell() { return Collections.unmodifiableList(this.bulletHell); }

    public AbstractMapObject objectAt(Vector pos) { return this.instances.get(pos); }

    public Particle particleAt(Vector pos) { return this.particleMap.get(pos); }

    public boolean isPlayerAlive() { return this.playerTank.isAlive(); }

    public void addObject(AbstractMapObject o) {
        this.instances.put(o.getPosition(), o);
        o.addPositionObserver(this);
        o.addKiller(this);
        if (o.getClass() == Bullet.class) {
            this.bulletHell.add((Bullet) o);
        } else if (o.getClass() == Schutzengrabenvernichtungspanzerkraftwagen.class) {
            this.enemyTanks.add((Schutzengrabenvernichtungspanzerkraftwagen) o);
        }
    }

    public void addParticle(Particle o) {
        this.particleMap.put(o.getPosition(), o);
        o.addKiller(this);
    }

    @Override
    public void positionChanged(Vector oldPos, Vector newPos, AbstractMapObject caller) {
        this.instances.remove(oldPos);
        this.instances.put(newPos, caller);
    }

    @Override
    public void killed(AbstractMapObject caller) {
        if (caller.getClass() == Particle.class) {
            this.particleMap.remove(caller.getPosition());
        } else {
            this.instances.remove(caller.getPosition());
            if (caller.getClass() == Schutzengrabenvernichtungspanzerkraftwagen.class) {
                this.enemyTanks.remove(caller);
            } else if (caller.getClass() == Bullet.class) {
                this.bulletHell.remove(caller);
            }
        }
    }
}
