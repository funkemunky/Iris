package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import org.bukkit.event.Event;

/**
 * Created by George on 09/03/2019 at 10:12.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.FLYING,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class InventoryMove extends Check {

    public InventoryMove(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private Verbose verbose = new Verbose();

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if (getData().getLastInvClick().hasNotPassed(5) && getData().getMovementProcessor().isServerOnGround() && getData().getMovementProcessor().getDeltaXZ() > 0.2
                && verbose.flag(3, 250L)) {
            flag("t: " + verbose.getVerbose(), true, false);
        }

        debug("Last Inv Click: " + getData().getLastInvClick() + ", Verbose: " + verbose.getVerbose());
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
