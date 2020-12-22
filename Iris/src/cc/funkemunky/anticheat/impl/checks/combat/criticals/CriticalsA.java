package cc.funkemunky.anticheat.impl.checks.combat.criticals;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import lombok.val;
import org.bukkit.event.Event;

@Packets(packets = {Packet.Client.POSITION_LOOK, Packet.Client.POSITION, Packet.Client.LEGACY_POSITION, Packet.Client.LEGACY_POSITION_LOOK})
public class CriticalsA extends Check {
    public CriticalsA(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        if(getData().getPlayer().getAllowFlight() || getData().getMovementProcessor().getLastVehicle().hasNotPassed(6) || getData().getMovementProcessor().getLastRiptide().hasNotPassed(5) || getData().getLastAttack().hasPassed() || getData().getMovementProcessor().getGroundTicks() < 5) return;

        val isGoodToGo = getData().getBoundingBox().grow(1, 0.1f,1).getCollidingBlockBoxes(getData().getPlayer()).stream().allMatch(box -> box.getMaximum().lengthSquared() > 2.4);
        if(isGoodToGo && getData().getMovementProcessor().getDeltaY() < 0.3 && !getData().getMovementProcessor().isHalfBlocksAround() && getData().getMovementProcessor().getDeltaY() > 0) {
            flag(getData().getMovementProcessor().getDeltaY() + ">-0", true, true);
        }
    }

    @Override
    public void onBukkitEvent(Event event) {
    }
}
