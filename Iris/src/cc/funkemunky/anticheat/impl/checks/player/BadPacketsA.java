package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInAbilitiesPacket;
import cc.funkemunky.api.tinyprotocol.packet.out.WrappedOutAbilitiesPacket;
import org.bukkit.event.Event;

@Packets(packets = {
        Packet.Client.ABILITIES,
        Packet.Server.ABILITIES,
        Packet.Client.POSITION,
        Packet.Client.POSITION_LOOK,
        Packet.Client.LEGACY_POSITION,
        Packet.Client.LEGACY_POSITION_LOOK,})
public class BadPacketsA extends Check {

    private boolean dontFlag, clientSent;

    public BadPacketsA(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        switch(packetType) {
            case Packet.Server.ABILITIES: {
                WrappedOutAbilitiesPacket abilities = new WrappedOutAbilitiesPacket(packet, getData().getPlayer());

                if (abilities.isAllowedFlight()) {
                    dontFlag = true;
                }
                break;
            }
            case Packet.Client.ABILITIES: {
                WrappedInAbilitiesPacket abilities = new WrappedInAbilitiesPacket(packet, getData().getPlayer());

                if (abilities.isAllowedFlight()) {
                    clientSent = true;
                } else {
                    dontFlag = clientSent = false;
                }
                break;
            }
            default: {
                if (!dontFlag && clientSent) {
                    flag("Fake abilities packet", true, true);
                }
                break;
            }
        }
    }

    @Override
    public void onBukkitEvent(Event event) {

    }
}
