package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.BukkitEvents;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.utils.MathUtils;
import lombok.val;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

@BukkitEvents(events = {PlayerMoveEvent.class})
public class BadPacketsF extends Check {

    public BadPacketsF(String name, CancelType cancelType, int maxVL, boolean enabled, boolean executable, boolean cancellable) {
        super(name, cancelType, maxVL, enabled, executable, cancellable);
    }

    @Setting(name = "maxMovement")
    private double maxMovement = 5.0;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {

    }

    @Override
    public void onBukkitEvent(Event event) {
        PlayerMoveEvent e = (PlayerMoveEvent) event;

        val distance = e.getTo().distance(e.getFrom());

        if(distance > maxMovement) {
            flag(MathUtils.round(distance, 4) + ">-" + maxMovement, true, true);
        }
    }
}
