package best.reich.ingros.util.game;

public final class TickTimerUtil {

    private int tick;

    public void update() {
        tick++;
    }

    public void reset() {
        tick = 0;
    }

    public boolean hasTimePassed(final int ticks) {
        return tick >= ticks;
    }
}