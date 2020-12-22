package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.data.processors.MovementProcessor;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.PlayerUtils;
import org.bukkit.event.Event;

/**
 * Created by George on 24/03/2019 at 11:12.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class LiquidWalk extends Check {

    public LiquidWalk(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private Verbose verbose = new Verbose();

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        MovementProcessor move = getData().getMovementProcessor();

        if (move.isInLiquid() && BlockUtils.isLiquid(getData().getPlayer().getLocation().subtract(0, 1, 0).getBlock()) && !move.isInWeb() && !getData().isGeneralCancel()) {
            double threshold = move.getLiquidTicks() > 50 ? 0.14 : 0.18;

            if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
                threshold += (float) (double) 0.05;
            }

            threshold += PlayerUtils.getDepthStriderLevel(getData().getPlayer()) * 0.06;

            if (move.getDeltaXZ() > threshold) {
                if (verbose.flagB(10, 2)) {
                    flag(MathUtils.round(move.getDeltaXZ(), 4) + " > " + MathUtils.trim(3, threshold), true, true);
                }
            } else {
                verbose.deduct();
            }

            debug("Threshold: " + threshold + ", DeltaXZ: " + move.getDeltaXZ());
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }


}
