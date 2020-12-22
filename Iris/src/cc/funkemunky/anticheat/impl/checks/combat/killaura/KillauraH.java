package cc.funkemunky.anticheat.impl.checks.combat.killaura;

import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.api.utils.MathUtils;
import org.bukkit.event.Event;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import lombok.val;
import org.bukkit.event.Event;

@Packets(packets = {
        Packet.Client.POSITION_LOOK,
        Packet.Client.LOOK,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_LOOK})
public class KillauraH extends Check {
    public KillauraH(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private float lastPitchDelta, lastYawDelta;
    private double vl;
    private long lastGCD;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if(getData().getLastAttack().hasNotPassed(4)) {
            val to = getData().getMovementProcessor().getTo();
            val from = getData().getMovementProcessor().getFrom();
            val pitchDifference = Math.abs(from.getPitch() - to.getPitch());
            val yawDifference = Math.abs(from.getYaw() - to.getYaw());

            val offset = 16777216L;
            val pitchGCD = MiscUtils.gcd((long) (pitchDifference * offset), (long) (lastPitchDelta * offset));

            if (Math.abs(to.getPitch()) < 85.0f && yawDifference > 0.4 && pitchDifference > 0 && pitchGCD > 10000 && lastGCD == pitchGCD && getData().getMovementProcessor().getOptifineTicks() < 20) {
                if((vl+=2) > 60) {
                    flag(String.valueOf(pitchGCD / 2000), true, true);
                }
            } else {
                vl -= vl > 0 ? 1 : 0;
            }

            debug("VL: " + vl + " PITCH: " + pitchGCD + " OPTIFINE: " + getData().isCinematicMode());

            lastPitchDelta = pitchDifference;
            lastYawDelta = yawDifference;
            lastGCD = pitchGCD;
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
