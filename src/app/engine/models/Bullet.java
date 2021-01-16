package app.engine.models;

import app.engine.tools.Orientation;
import app.engine.tools.Vector;

public class Bullet extends AbstractMapObject {
    public Bullet(Vector pos, Orientation orientation) {
        super.position = pos;
        super.orient = orientation;
    }

    public void move(Vector pos) {
        super.callObservers(super.position, pos);
        super.position = pos;
    }
}
