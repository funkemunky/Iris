package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.data.processors.MovementProcessor;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.TickTimer;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.BlockUtils;
import org.bukkit.event.Event;

/**
 * Created by George on 24/03/2019 at 11:40.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class LiquidWalkB extends Check {

    public LiquidWalkB(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private Verbose verbose = new Verbose();
    private TickTimer lastServerOnGround = new TickTimer(50);

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        MovementProcessor move = getData().getMovementProcessor();

        if (move.isInLiquid() && BlockUtils.isLiquid(getData().getPlayer().getLocation().subtract(0, 1, 0).getBlock()) && !move.isInWeb() && !getData().isGeneralCancel()
                && lastServerOnGround.hasPassed(20)) { // lastServerOnGround is for players travelling on the ground underwater - we use the server check for this so it is not modified by the client.
            if (move.getDeltaY() == 0.00f) { // 0 Y difference in water is impossible for players using Vanilla to replicate.
                if (verbose.flagB(20, 2)) { // Add some verbose (sometimes Y difference is 0 for a couple of packets)
                    flag("", true, true);
                }
            } else {
                verbose.deduct(); // Deduct the verbose if their y difference is not 0
            }
        }

        if (move.isServerOnGround()) {
            lastServerOnGround.reset();
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }

}
