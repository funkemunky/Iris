package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import lombok.val;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.FLYING,
        Packet.Client.POSITION,
        Packet.Client.POSITION_LOOK,
        Packet.Client.LOOK,
        Packet.Client.LEGACY_POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,
        Packet.Client.LEGACY_LOOK})
public class TimerB extends Check {
    public TimerB(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Setting(name = "threshold")
    private long threshold = 1980L;

    @Setting(name = "maxVl")
    private int maxVl = 2;

    private int vl, ticks;
    private long start;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if(ticks++ >= 40) {
            val delta = timeStamp - start;
            if(delta < threshold) {
                if(vl++ > maxVl) {
                    flag(delta + "<-" + threshold, true, true);
                }
            } else vl-= vl > 0 ? 1 : 0;
            start = timeStamp;
            ticks = 0;
        } else ticks++;
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
