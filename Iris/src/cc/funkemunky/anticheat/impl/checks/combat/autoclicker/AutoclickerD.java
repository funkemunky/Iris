package cc.funkemunky.anticheat.impl.checks.combat.autoclicker;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.ARM_ANIMATION})
public class AutoclickerD extends Check {

    @Setting(name = "maxCPS")
    private int maxCps = 20;

    @Setting(name = "banCPS")
    private int banCPS = 30;

    private long start;
    private int clicks;

    public AutoclickerD(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if(!MiscUtils.shouldReturnArmAnimation(getData())) {
            if(timeStamp - start >= 1000L) {
                if(clicks > maxCps) flag(clicks + ">-" + maxCps, true, clicks > banCPS);
                debug("CPS: " + clicks);
                clicks = 0;
                start = timeStamp;
            } else clicks++;
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
