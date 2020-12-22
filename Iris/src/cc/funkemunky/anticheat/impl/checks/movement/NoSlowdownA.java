package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.PlayerUtils;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by George on 03/03/2019 at 11:33.
 */
@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class NoSlowdownA extends Check {

    private Verbose verbose = new Verbose();

    public NoSlowdownA(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if (!getData().isGeneralCancel() && getData().getActionProcessor().isUsingItem() && getData().getVelocityProcessor().getLastVelocity().hasPassed(40)
                && getData().getLastStopUsingItem().hasPassed(5)) {
            double deltaXZ = getData().getMovementProcessor().getDeltaXZ();
            double threshold = getData().getMovementProcessor().isClientOnGround() ? 0.07 : 0.18;
            threshold += PlayerUtils.getPotionEffectLevel(getData().getPlayer(), PotionEffectType.SPEED) * (getData().getMovementProcessor().isClientOnGround() ? 0.06 : 0.03);
            threshold += getData().getMovementProcessor().isOnIce() ? 0.06 : 0;
            threshold += getData().getMovementProcessor().isInLiquid() && getData().getMovementProcessor().isClientOnGround() ? 0.05 : 0;

            if (deltaXZ > threshold) {
                if (verbose.flag(20, 500L, 2)) {
                    flag(MathUtils.round(deltaXZ, 3) + " > " + threshold, true, true);
                } else {
                    verbose.deduct();
                }
            }
            debug("DeltaXZ: " + deltaXZ + ", Threshold: " + threshold);
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
