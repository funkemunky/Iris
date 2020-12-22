package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import org.bukkit.event.Event;

/**
 * Created by George on 07/03/2019 at 18:45.
 */
@Packets(packets = {Packet.Client.WINDOW_CLICK})
public class FastInventory extends Check {

    public FastInventory(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private Verbose verbose = new Verbose();
    private long lastInvClick;
    private long lastElapsed;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        long elapsed = MathUtils.elapsed(lastInvClick);
        long delta = Math.abs(elapsed - lastElapsed);

        if (elapsed > 0 && delta <= 20 && verbose.flag(10, 500L, 2)) {
            flag(delta + " <= 20", true, true);
        } else {
            verbose.deduct();
        }

        debug("Elapsed: " + elapsed + ", Delta: " + delta);

        lastInvClick = System.currentTimeMillis();
        lastElapsed = elapsed;
    }

    @Override
    public void onBukkitEvent(Event event) {

    }

}
