package app.engine.tools;

/**
 * Various algebraic tools used in different places inside the project
 * @author Mateusz Praski
 */
public class Algebra {
    /**
     * Transform Vector by a angle clockwise
     * @param pos Vector from (0,0) to the bottom left
     * @param degree angle in degrees
     * @param centerVec Vector from the bottom left to the middle of the object
     * @return new Vector transformed by a degree clockwise
     */
    public static Vector rotateVector(Vector pos, double degree, Vector centerVec) {
        pos = pos.add(centerVec);
        double radians = Math.toRadians(degree);
        return new Vector(
                (int) Math.round((pos.x * Math.cos(radians) + pos.y * Math.sin(radians))),
                (int) Math.round((pos.y * Math.cos(radians) - pos.x * Math.sin(radians)))
        ).add(centerVec.opposite());
    }
}
