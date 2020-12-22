package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.data.processors.MovementProcessor;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import org.bukkit.event.Event;

/**
 * Created by George on 24/03/2019 at 11:40.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class Step extends Check {

    public Step(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }


    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        MovementProcessor move = getData().getMovementProcessor();

        if (!MiscUtils.cancelForFlight(getData()) && move.getDeltaY() == 1.0) { // Apparently clients still do this.
            flag("", false, false);
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }

}
