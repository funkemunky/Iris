package cc.funkemunky.anticheat.api.utils;

import cc.funkemunky.anticheat.Iris;


public class TickTimer {
    private int ticks = 0, defaultPassed;

    public TickTimer(int defaultPassed) {
        this.defaultPassed = defaultPassed;
    }

    public void reset() {
        ticks = Iris.getInstance().getCurrentTicks();
    }

    public boolean hasPassed() {
        return Iris.getInstance().getCurrentTicks() - ticks > defaultPassed;
    }

    public boolean hasPassed(int amount) {
        return Iris.getInstance().getCurrentTicks() - ticks > amount;
    }

    public boolean hasNotPassed() {
        return Iris.getInstance().getCurrentTicks() - ticks <= defaultPassed;
    }

    public boolean hasNotPassed(int amount) {
        return Iris.getInstance().getCurrentTicks() - ticks <= amount;
    }

    public int getPassed() {
        return Iris.getInstance().getCurrentTicks() - ticks;
    }
}
