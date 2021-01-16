package app.engine.models;

import app.engine.tools.Orientation;
import app.engine.tools.Vector;

public class Rock extends AbstractMapObject {
    public Rock(Vector pos) {
        super.HP = 2;
        super.position = pos;
    }

    @Override
    public void kill() {
        this.HP--;
        if (this.HP <= 0) {
            super.kill();
        }
    }
}
