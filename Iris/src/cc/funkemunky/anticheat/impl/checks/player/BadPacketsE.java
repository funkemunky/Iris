package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.BLOCK_DIG})
public class BadPacketsE extends Check {
    public BadPacketsE(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private long lastTimeStamp;
    private int vl;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        WrappedInBlockDigPacket dig = new WrappedInBlockDigPacket(packet, getData().getPlayer());

        if (dig.getAction().equals(WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK)) {
            long elapsed = timeStamp - lastTimeStamp;

            if (elapsed <= 2) {
                if (vl++ > 3) {
                    flag("t: " + elapsed, true, true);
                }
            } else {
                vl = 0;
            }

            lastTimeStamp = timeStamp;
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
