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

    private final int scorePerEnemy = 100;
    private int currentScore = 0;

    private final int maxEnemyInterval;
    private int lastEnemy = 0;

    private final int maxRockInterval;
    private int lastRock = 0;

    private final int maxHP;
    private final WorldMap map;

    private List<AbstractMapObject> garbage = new ArrayList<>();
    private int day = 0;

    public World(int maxRock, int maxEnemy, int maxLives) {
        this(maxRock, maxEnemy, 12, 5, maxLives);
    }

    public World(int maxRock, int maxEnemy, int maxAbs, int minAbs, int maxLives) {
        this.maxHP = maxLives;
        this.maxRockInterval = maxRock;
        this.maxEnemyInterval = maxEnemy;
        this.maxAbsolute = maxAbs;
        this.minAbsolute = minAbs;
        this.map = new WorldMap(maxHP);
    }

    public Vector getPlayerPos() { return this.map.getPlayerTank().getPosition(); }

    public Orientation getPlayerOrient() { return this.map.getPlayerTank().getOrient(); }

    void takeAction() {
        for (IObserverDayChanged o : this.obs) {
            if (o.newDay(this.day)) {
                this.garbage.add((AbstractMapObject) o);
            }
        }
        this.dropGarbage();

        for (Bullet o : this.map.getBulletHell()) {
            this.moveBullet(o);
        }
        this.dropGarbage();

        for(Schutzengrabenvernichtungspanzerkraftwagen enemy : this.map.getEnemyTanks()) {
            this.takeAIAction(enemy);
        }
        this.dropGarbage();

        this.rockSpawner();
        this.enemySpawner();

        this.day++;
    }

    void enemySpawner() {
        if (this.day - this.lastEnemy > this.maxEnemyInterval) {
            Vector tankPos = this.getRandomPos();
            if (tankPos == null) {
                return;
            }
            this.spawnObject(new Schutzengrabenvernichtungspanzerkraftwagen(tankPos));
            this.lastEnemy = this.day;
        } else if (this.worldGenerator.nextInt(this.maxEnemyInterval) == 0) {
            Vector tankPos = this.getRandomPos();
            if (tankPos == null) {
                return;
            }
            this.spawnObject(new Schutzengrabenvernichtungspanzerkraftwagen(tankPos));
            this.lastEnemy = this.day;
        }
    }

    void rockSpawner() {
        if (this.day - this.lastRock > this.maxRockInterval) {
            Vector rockPos = this.getRandomPos();
            if (rockPos == null) {
                return;
            }
            this.spawnObject(new Rock(rockPos));
            this.lastRock = this.day;
        } else if (this.worldGenerator.nextInt(this.maxRockInterval) == 0) {
            Vector rockPos = this.getRandomPos();
            if (rockPos == null) {
                return;
            }
            this.spawnObject(new Rock(rockPos));
            this.lastRock = this.day;
        }
    }

    void takeAIAction(Schutzengrabenvernichtungspanzerkraftwagen enemy) {
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
        int totalTrials = 0;
        do {
            tankPos = new Vector(
                    this.worldGenerator.nextInt(2 * this.maxAbsolute) + (playerPos.x - this.maxAbsolute),
                    this.worldGenerator.nextInt(2 * this.maxAbsolute) + (playerPos.y - this.maxAbsolute)
            );
            totalTrials++;
            if (totalTrials > 100) {
                return null;
            }
        } while((playerPos.getMaxDist(tankPos) > this.minAbsolute && playerPos.getMaxDist(tankPos) < this.maxAbsolute && this.isEmptyTile(tankPos)));
        return tankPos;
    }

//    void spawnEnemy() {
//
//    }

    void moveBullet(Bullet o) {
        Vector newPos = o.getPosition().add(o.getOrient().getUnitVector());
        if (this.isEmptyTile(newPos)) {
            o.move(newPos);
        } else {
            this.createExplosion(newPos);
            this.garbage.add(o);
            this.garbage.add(this.objectAt(newPos));
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

    public void shoot(Schutzengrabenvernichtungspanzerkraftwagen tank) {
        Vector bulletPos = tank.getPosition().add(tank.getOrient().getUnitVector());
        AbstractMapObject targetObject = this.map.objectAt(bulletPos);
        if (targetObject == null) {
            Bullet boom = new Bullet(bulletPos, tank.getOrient());
            this.map.addObject(boom);
            this.createFire(tank.getPosition(), tank.getOrient());
        } else {
            this.garbage.add(targetObject);
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

    private void dropGarbage() {
        for(AbstractMapObject o : this.garbage) {
            o.kill();
            if (o.getClass() == Schutzengrabenvernichtungspanzerkraftwagen.class) {
                this.currentScore += this.scorePerEnemy;
            }
        }
        this.garbage = new ArrayList<>();
    }

    @Override
    public void killed(AbstractMapObject caller) {
        Particle p = (Particle) caller;
        this.obs.remove(p);
    }

    public int getScore() {
        if (this.getPlayerHP() > 0) {
            return this.currentScore;
        } else {
            return this.currentScore - this.scorePerEnemy;
        }
    }
}
