package app.engine.tools;

import app.engine.models.Particle;
import com.sun.javafx.geom.transform.BaseTransform;

/**
 * Enum describing Orientation on a map (assuming that axis center is top left corner)
 * @author Mateusz Praski
 */
public enum Orientation {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    /**
     *
     * @return Orientation representation as an integer
     */
    public int getInt() {
        return switch (this) {
            case NORTH -> 0;
            case NORTHEAST -> 1;
            case EAST -> 2;
            case SOUTHEAST -> 3;
            case SOUTH -> 4;
            case SOUTHWEST -> 5;
            case WEST -> 6;
            case NORTHWEST -> 7;
        };
    }

    /**
     *
     * @return Angle in degrees between given orientation and North clockwise
     */
    public double getDegree() {
        return switch(this) {
            case NORTH -> 0;
            case NORTHEAST -> 45;
            case EAST -> 90;
            case SOUTHEAST -> 135;
            case SOUTH -> 180;
            case SOUTHWEST -> 225;
            case WEST -> 270;
            case NORTHWEST -> 315;
        };
    }

    /**
     *
     * @return (as unit as integer vector can be) unit vector representing given orientation
     */
    public Vector getUnitVector() {
        return switch(this) {
            case NORTH -> new Vector(0, -1);
            case NORTHEAST -> new Vector(1, -1);
            case EAST -> new Vector(1, 0);
            case SOUTHEAST -> new Vector(1, 1);
            case SOUTH -> new Vector(0, 1);
            case SOUTHWEST -> new Vector(-1, 1);
            case WEST -> new Vector(-1, 0);
            case NORTHWEST -> new Vector(-1, -1);
        };
    }

    /**
     *
     * @param x Integer representation of the orientation
     * @return Orientation
     */
    public static Orientation getOrient(int x) {
        return switch(x % 8) {
            case 0 -> NORTH;
            case 1 -> NORTHEAST;
            case 2 -> EAST;
            case 3 -> SOUTHEAST;
            case 4 -> SOUTH;
            case 5 -> SOUTHWEST;
            case 6 -> WEST;
            case 7 -> NORTHWEST;
            default -> NORTH;
        };
    }

    /**
     *
     * @param x First orientation
     * @param y Second orientation
     * @return First orientation rotated by second orientation clockwise
     */
    public static Orientation translateOrient(Orientation x, Orientation y) {
        return getOrient(x.getInt() + y.getInt());
    }

    /**
     *
     * @param x First orientation
     * @param y Second orientation
     * @return First orientation rotated by second orientation clockwise
     */
    public static Orientation translateOrient(Orientation x, int y) {
        return getOrient(x.getInt() + y);
    }

    public static Orientation getFromDegrees(double degrees) {
        degrees %= 360;
        if (degrees > 337.5 || degrees < 22.5) {
            return Orientation.NORTH;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            return Orientation.NORTHEAST;
        } else if(degrees >= 67.5 && degrees < 112.5) {
            return Orientation.EAST;
        } else if(degrees >= 112.5 && degrees < 157.5) {
            return Orientation.SOUTHEAST;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            return Orientation.SOUTH;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            return Orientation.SOUTHWEST;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            return Orientation.WEST;
        } else {
            return Orientation.NORTHWEST;
        }
    }
}
