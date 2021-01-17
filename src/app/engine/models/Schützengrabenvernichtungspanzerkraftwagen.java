package app.engine.models;

import app.engine.tools.Orientation;
import app.engine.tools.Vector;

public class Schützengrabenvernichtungspanzerkraftwagen extends AbstractMapObject {

    public Schützengrabenvernichtungspanzerkraftwagen() {
        this(new Vector(0, 0)); }

    public Schützengrabenvernichtungspanzerkraftwagen(Vector position) {
        super.position = position;
    }

    public void move(Vector pos) {
        super.callObservers(super.position, pos);
        super.position = pos; 
    }

    public void setHP(int HP) { super.HP = HP; }

    public int getHP() { return super.HP; }

    public void rotate(Orientation o) { super.orient = Orientation.translateOrient(super.orient, o); }

    public void setOrient(Orientation o) { super.orient = o; }

    @Override
    public void kill() {
        super.HP--;
        if (super.HP <= 0) {
            super.kill();
        }
    }

    public boolean isAlive() { return super.HP > 0; }
}
