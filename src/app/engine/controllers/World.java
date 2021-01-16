package app.engine.controllers;

import app.engine.interfaces.IObserverDayChanged;
import app.engine.interfaces.IObserverKilled;
import app.engine.interfaces.ParticleType;
import app.engine.models.*;
import app.engine.tools.Orientation;
import app.engine.tools.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class World implements IObserverKilled {
    private final Random worldGenerator = new Random();
    private final List<IObserverDayChanged> obs = new ArrayList<>();
    private final int maxAbsolute;
    private final int minAbsolute;

    private final int maxEnemyInterval;
    private int lastEnemy = 0;

    private final int maxRockInterval;
    private int lastRock = 0;

    private final int maxHP = 3;
    private final WorldMap map = new WorldMap(maxHP);

    private int day = 0;
    public World() { this(8, 10); }

    public World(int maxRock, int maxEnemy) { this(maxRock, maxEnemy, 12, 5); }

    public World(int maxRock, int maxEnemy, int maxAbs, int minAbs) {
        this.maxRockInterval = maxRock;
        this.maxEnemyInterval = maxEnemy;
        this.maxAbsolute = maxAbs;
        this.minAbsolute = minAbs;
    }

    public Vector getPlayerPos() { return this.map.getPlayerTank().getPosition(); }

    public Orientation getPlayerOrient() { return this.map.getPlayerTank().getOrient(); }

    void takeAction() {
        List<IObserverDayChanged> killed = new ArrayList<>();
        for (IObserverDayChanged o : this.obs) {
            if (o.newDay(this.day)) {
                killed.add(o);
            }
        }
        for (IObserverDayChanged o : killed) {
            o.kill();
        }

        for(Schützengrabenvernichtungspanzerkraftwagen enemy : this.map.getEnemyTanks()) {
            this.takeAIAction(enemy);
        }

        List<Bullet> killedBullets = new ArrayList<>();
        for (Bullet o : this.map.getBulletHell()) {
            if (this.moveBullet(o)) {
                killedBullets.add(o);
            }
        }
        for (Bullet o : killedBullets) {
            o.kill();
        }

        this.rockSpawner();
        this.enemySpawner();

        this.day++;
    }

    void enemySpawner() {
        if (this.day - this.lastEnemy > this.maxEnemyInterval) {
            Vector tankPos = this.getRandomPos();
            this.spawnObject(new Schützengrabenvernichtungspanzerkraftwagen(tankPos));
            this.lastEnemy = this.day;
        } else if (this.worldGenerator.nextInt(this.maxEnemyInterval) == 0) {
            Vector tankPos = this.getRandomPos();
            this.spawnObject(new Schützengrabenvernichtungspanzerkraftwagen(tankPos));
            this.lastEnemy = this.day;
        }
    }

    void rockSpawner() {
        if (this.day - this.lastRock > this.maxRockInterval) {
            Vector rockPos = this.getRandomPos();
            this.spawnObject(new Rock(rockPos));
            this.lastRock = this.day;
        } else if (this.worldGenerator.nextInt(this.maxRockInterval) == 0) {
            Vector rockPos = this.getRandomPos();
            this.spawnObject(new Rock(rockPos));
            this.lastRock = this.day;
        }
    }

    void takeAIAction(Schützengrabenvernichtungspanzerkraftwagen enemy) {
        Orientation closestOrient = Orientation.getFromDegrees(Math.toDegrees(3 * Math.PI/2.0 + Math.atan2(
                enemy.getPosition().y - this.getPlayerPos().y,
                enemy.getPosition().x - this.getPlayerPos().x
        )));
        enemy.setOrient(closestOrient);
        if (this.worldGenerator.nextBoolean()) {
            Vector newPos = enemy.getPosition().add(enemy.getOrient().getUnitVector());
            if (this.isEmptyTile(newPos)) {
                enemy.move(newPos);
            }
        } else {
            this.shoot(enemy);
        }
    }

    Vector getRandomPos() {
        Vector playerPos = this.getPlayerPos();
        Vector tankPos;
        do {
            tankPos = new Vector(
                    this.worldGenerator.nextInt(2 * this.maxAbsolute) + (playerPos.x - this.maxAbsolute),
                    this.worldGenerator.nextInt(2 * this.maxAbsolute) + (playerPos.y - this.maxAbsolute)
            );
        } while(playerPos.getMaxDist(tankPos) > this.minAbsolute && playerPos.getMaxDist(tankPos) < this.maxAbsolute && this.isEmptyTile(tankPos));
        return tankPos;
    }

//    void spawnEnemy() {
//
//    }

    boolean moveBullet(Bullet o) {
        Vector newPos = o.getPosition().add(o.getOrient().getUnitVector());
        if (this.isEmptyTile(newPos)) {
            o.move(newPos);
            return false;
        } else {
            this.createExplosion(newPos);
            this.objectAt(newPos).kill();
            return true;
        }
    }

    public void addNewDayObserver(IObserverDayChanged o) {
        this.obs.add(o);
    }

    public void movePlayerForward() {
        Vector newPos = this.map.getPlayerTank().getPosition().add(this.map.getPlayerTank().getOrient().getUnitVector());
        if (this.isEmptyTile(newPos)) {
            this.map.getPlayerTank().move(newPos);
        }
        this.takeAction();
    }

    public void movePlayerBackward() {
        Vector newPos = this.map.getPlayerTank().getPosition().add(this.map.getPlayerTank().getOrient().getUnitVector().opposite());
        if (this.isEmptyTile(newPos)) {
            this.map.getPlayerTank().move(newPos);
        }
        this.takeAction();
    }

    public boolean isEmptyTile(Vector pos) { return this.map.objectAt(pos) == null; }

    public AbstractMapObject objectAt(Vector pos) { return this.map.objectAt(pos); }

    public Particle particleAt(Vector pos) { return this.map.particleAt(pos); }

    public int getPlayerHP() { return this.map.getPlayerTank().getHP(); }

    public int getPlayerMaxHP() { return this.maxHP; }

    public void shoot() {
        this.takeAction();
        this.shoot(this.map.getPlayerTank());
    }

    public void shoot(Schützengrabenvernichtungspanzerkraftwagen tank) {
        Vector bulletPos = tank.getPosition().add(tank.getOrient().getUnitVector());
        AbstractMapObject targetObject = this.map.objectAt(bulletPos);
        if (targetObject == null) {
            Bullet boom = new Bullet(bulletPos, tank.getOrient());
            this.map.addObject(boom);
            this.createFire(tank.getPosition(), tank.getOrient());
        } else {
            tank.kill();
            targetObject.kill();
            this.createExplosion(tank.getPosition());
            this.createExplosion(bulletPos);
        }

    }

    public void createExplosion(Vector pos) {
        Particle x = new Particle(ParticleType.EXPLOSION, pos);
        this.addNewDayObserver(x);
        x.addKiller(this);
        this.map.addParticle(x);
    }

    public void createFire(Vector pos, Orientation orient) {
        Particle x = new Particle(ParticleType.FIRE, pos, orient);
        this.addNewDayObserver(x);
        this.map.addParticle(x);
    }

    public void spawnObject(AbstractMapObject o) { this.map.addObject(o); }

    public void rotatePlayer(Orientation translation) { this.map.getPlayerTank().rotate(translation); }

//    public boolean isPlayerAlive() { return this.map.isPlayerAlive(); }

    @Override
    public void killed(AbstractMapObject caller) {
        Particle p = (Particle) caller;
        this.obs.remove(p);
    }
}
