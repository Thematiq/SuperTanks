package app.engine.interfaces;

/**
 * Enum for particle type
 * @author Mateusz Praski
 */
public enum ParticleType {
    EXPLOSION,
    FIRE;
    public int getLifespan() {
        return switch(this) {
            case FIRE -> 1;
            case EXPLOSION -> 2;
        };
    }
}
