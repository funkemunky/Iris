package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.PlayerUtils;
import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

@Packets(packets = {
        Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_POSITION})
public class FlyB extends Check {
    public FlyB(String name, CancelType cancelType, int maxVL, boolean enabled, boolean executable, boolean cancellable) {
        super(name, cancelType, maxVL, enabled, executable, cancellable);
    }

    private int vl;
    private long lastTimeStamp;
    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        val move = getData().getMovementProcessor();

        if ((getData().getMovementProcessor().isBlocksOnTop() && getData().getMovementProcessor().getIceTicks() > 0) || move.isClimablesClose()) {
            return;
        }

        if (MiscUtils.cancelForFlight(getData(), 10) || timeStamp < lastTimeStamp + 5 || PlayerUtils.getPotionEffectLevel(getData().getPlayer(), PotionEffectType.JUMP) > 0
            || getData().getLastBlockPlace().hasNotPassed(20) || getData().getMovementProcessor().getLastVehicle().hasNotPassed(10)) return;

        if (!move.isServerOnGround()
                && move.getDeltaY() > move.getServerYVelocity() + 0.001
                && !MathUtils.approxEquals(0.002, move.getServerYAcceleration(), move.getClientYAcceleration())) {
            if ((!move.isNearGround() && move.getAirTicks() > 4) || vl++ > 4)
            flag(move.getDeltaY() + " > " + (move.getServerYVelocity() + 0.001), true, true);
        } else vl-= vl > 0 ? 1 : 0;
        debug("MOTIONY: " + MathUtils.round(move.getDeltaY(), 4) + " SERVERY: " + MathUtils.round(move.getServerYVelocity(), 4));
        lastTimeStamp = timeStamp;
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
