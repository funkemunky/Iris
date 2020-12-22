package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import org.bukkit.event.Event;

/**
 * Created by George on 15/03/2019 at 19:30.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class NoFallA extends Check {

    public NoFallA(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);

    }

    private long lastCancel;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if (!MiscUtils.cancelForFlight(getData()) && MathUtils.elapsed(lastCancel, 500L) && !getData().getMovementProcessor().isOnSlimeBefore()) {
            if (getData().getMovementProcessor().getRealFallDistance() - getData().getPlayer().getFallDistance() > (getData().isLagging() ? 10 : 4)) {
                flag(getData().getMovementProcessor().getRealFallDistance() + " > " + getData().getPlayer().getFallDistance(), true, true);
                lastCancel = System.currentTimeMillis();
            }
            debug("Real Fall Distance: " + getData().getMovementProcessor().getRealFallDistance() + ", Player Fall Distance: " + getData().getPlayer().getFallDistance());
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
