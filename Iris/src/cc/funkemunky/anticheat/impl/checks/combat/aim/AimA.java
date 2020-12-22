package cc.funkemunky.anticheat.impl.checks.combat.aim;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import lombok.val;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Packets(packets = {Packet.Client.LOOK, Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK, Packet.Client.LEGACY_LOOK})
public class AimA extends Check {
    public AimA(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private List<Float> pitchMatch = new ArrayList<>();
    private int vl;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        val move = getData().getMovementProcessor();
        val pitchDelta = move.getPitchDelta();
        val yawDelta = move.getYawDelta();

        if (yawDelta > 1.0f) {
            if (move.getOptifineTicks() > 1) {
                pitchMatch.clear();
            } else {
                pitchMatch.add(pitchDelta);

                if (pitchMatch.size() == 100) {
                    List<Float> distinctList = pitchMatch.stream().distinct().collect(Collectors.toList());
                    int duplicates = pitchMatch.size() - distinctList.size();
                    long count = pitchMatch.stream().distinct().count();

                    if (duplicates <= 9) {
                        if (vl++ >= 5) {
                            flag("D -> " + duplicates + "C -> " + count, true, true);
                        }
                    } else {
                        vl-= vl > 0 ? 2 : 0;
                    }

                    debug("VL: " + vl + " DUP: " + duplicates);
                    pitchMatch.clear();
                    distinctList.clear();
                }
            }
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
