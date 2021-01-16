package app.engine.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class describing 2-dimensional vector
 * @author Mateusz Praski
 */
public class Vector {
    public final int x;
    public final int y;

    /**
     * Creates new Vector object
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return Vector opposite to calling vector
     */
    public Vector opposite() {return new Vector(-this.x, -this.y); }

    /**
     *
     * @param other Second vector in addition
     * @return Sum of two vectors
     */
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    /**
     *
     * @param scalar Vector scalar
     * @return Vector multiplied by a scalar
     */
    public Vector mult(int scalar) {return new Vector(this.x * scalar, this.y * scalar); }

    /**
     *
     * @param other Second vector
     * @return True if a given vector has both coordinates equal or smaller than the second
     */
    public boolean precedes(Vector other) {
        return this.x <= other.x && this.y <= other.y;
    }

    /**
     *
     * @param other Second vector
     * @return True if a given vector has both coordinates equal or larger than the second
     */
    public boolean follows(Vector other) {
        return this.x >= other.x && this.y >= other.y;
    }

    /** Wraps vector within border vector
     *  Used in map wrapping
     *
     * @param border vector limiting size
     * @return Vector with coordinates equal or smaller than the border vector using wrapping
     */
    public Vector wrap(Vector border) {
        int new_x;
        int new_y;
        if (this.x > border.x) {
            new_x = this.x - border.x - 1;
        } else if (this.x < 0) {
            new_x = border.x + this.x + 1;
        } else {
            new_x = x;
        }
        if (this.y > border.y) {
            new_y = this.y - border.y - 1;
        } else if (this.y < 0) {
            new_y = border.y + this.y + 1;
        } else {
            new_y = y;
        }
        return new Vector(new_x, new_y);
    }

    /**
     * Returns list of neighbouring vectors, within given vector
     * @param bottomLeft Bottom left vector, all returned must follow him
     * @param topRight Top right vector, all returned must precede him
     * @return List of neighbouring vectors
     */
    public List<Vector> getNeighbours(Vector bottomLeft, Vector topRight) {
        List<Vector> out = new ArrayList<>();
        Vector potential;
        for (int x = this.x-1; x <= this.x+1; ++x) {
            for (int y = this.y-1; y <= this.y+1; ++y) {
                potential = new Vector(x, y);
                if (potential.follows(bottomLeft) && potential.precedes(topRight) && !potential.equals(this)) {
                    out.add(potential);
                }
            }
        }
        return out;
    }

    /**
     *
     * @return list of all neighbouring vectors
     */
    public List<Vector> getNeighbours() {
        List<Vector> out = new ArrayList<>();
        Vector potential;
        for (int x = this.x-1; x <= this.x+1; ++x) {
            for (int y = this.y-1; y <= this.y+1; ++y) {
                potential = new Vector(x, y);
                if (!potential.equals(this)) {
                    out.add(potential);
                }
            }
        }
        return out;
    }

    public int getTaxiDist(Vector other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public int getMaxDist(Vector other) {
        return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return x == vector.x &&
                y == vector.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
