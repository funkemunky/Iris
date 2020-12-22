package cc.funkemunky.anticheat.impl.checks.movement;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.data.processors.MovementProcessor;
import cc.funkemunky.anticheat.api.utils.MiscUtils;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

@Packets(packets = {Packet.Client.POSITION_LOOK, Packet.Client.POSITION, Packet.Client.LEGACY_POSITION, Packet.Client.LEGACY_POSITION_LOOK})
public class FlyE extends Check {
    public FlyE(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        MovementProcessor move = getData().getMovementProcessor();

        if (MiscUtils.cancelForFlight(getData(), 15) || move.isWallBlocksClose()) return;

        Player player = getData().getPlayer();

        double totalMaxY = 0.43 + PlayerUtils.getPotionEffectLevel(player, PotionEffectType.JUMP) * 0.12f;

        if (move.getDeltaY() > totalMaxY) {
            flag(move.getDeltaY() + " > " + totalMaxY, true,true);
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
