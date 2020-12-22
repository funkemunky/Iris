package cc.funkemunky.anticheat.impl.checks.combat.reach;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.CustomLocation;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.val;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Packets(packets = {Packet.Client.POSITION_LOOK,
        Packet.Client.POSITION,
        Packet.Client.LOOK,
        Packet.Client.FLYING,
        Packet.Client.LEGACY_LOOK,
        Packet.Client.LEGACY_POSITION,
        Packet.Client.LEGACY_POSITION_LOOK})
public class ReachC extends Check {

    @Setting(name = "boxExpand")
    private float boxExpand = 3.0f;

    @Setting(name = "range")
    private long range = 200;

    @Setting(name = "threshold.vl")
    private double vlMax = 7;

    private double vl;

    public ReachC(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {
        val target = getData().getTarget();

        if (getData().getPlayer().getGameMode().toString().contains("CREATIVE") || getData().getPlayer().getAllowFlight()) return;

        if(target == null || getData().getLastAttack().hasPassed(2)) return;

        val entityData = target.getType().equals(EntityType.PLAYER) ? Iris.getInstance().getDataManager().getPlayerData(target.getUniqueId()) : null;

        val pastLocation = (entityData != null ? entityData.getMovementProcessor().getPastLocation() : getData().getEntityPastLocation());

        if(pastLocation.getPreviousLocations().size() == 0) return;
        List<CustomLocation> locations = pastLocation.getEstimatedLocation(getData().getTransPing(), range + Math.abs(getData().getLastTransPing() - getData().getTransPing()));
        List<BoundingBox> boxes = new ArrayList<>();
        val player = getData().getPlayer();
        val origin = getData().getMovementProcessor().getTo().clone().toLocation(player.getWorld()).add(0, 1.53, 0).toVector();

        val playerBox = new BoundingBox(origin, origin).grow(boxExpand, boxExpand, boxExpand);

        locations.forEach(loc -> boxes.add(getHitbox(target.getType(), loc)));

        val count = boxes.stream().filter(box -> box.collides(playerBox)).count();

        val entityHitBox = getHitbox(target.getType(), pastLocation.getPreviousLocation(getData().getTransPing()));

        entityHitBox.maxY = (float) origin.getY();

        val distance = MathUtils.round(MathUtils.getDistanceToBox(origin, entityHitBox), 4) - 1;
        if (count == 0) {
            if (vl++ > vlMax) {
                flag("DISTANCE: " + distance + " VL: " + vl, false, true);
            }
            debug("VL: " + vl + "REACH: " + boxExpand + " RANGE: " + 200);
        } else {
            vl -= vl > 0 ? 0.5f : 0;
        }

        debug("COUNT: " + count + " VL: " + vl + "/" + vlMax + " DISTANCE: " + distance);
    }

    @Override
    public void onBukkitEvent(Event event) {

    }

    private BoundingBox getHitbox(EntityType type, CustomLocation l) {
        if(!MiscUtils.entityDimensions.containsKey(type)) return new BoundingBox(l.toVector(), l.toVector()).grow(2,2,2);
        val entityVector = MiscUtils.entityDimensions.get(type);
        float minX = (float)Math.min(-entityVector.getX() + l.getX(), entityVector.getX() + l.getX());
        float minY = (float)Math.min(l.getY(), entityVector.getY() + l.getY());
        float minZ = (float)Math.min(-entityVector.getZ() + l.getZ(), entityVector.getZ() + l.getZ());
        float maxX = (float)Math.max(-entityVector.getX() + l.getX(), entityVector.getX() + l.getX());
        float maxY = (float)Math.max(l.getY(), entityVector.getY() + l.getY());
        float maxZ = (float)Math.max(-entityVector.getZ() + l.getZ(), entityVector.getZ() + l.getZ());
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ).grow(.25f, 0.3f, .25f);
    }
}
