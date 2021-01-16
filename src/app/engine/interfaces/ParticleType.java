package app.engine.interfaces;

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
