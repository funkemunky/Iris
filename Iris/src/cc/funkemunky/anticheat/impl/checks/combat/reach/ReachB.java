package cc.funkemunky.anticheat.impl.checks.combat.reach;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.BukkitEvents;
import cc.funkemunky.anticheat.api.utils.DynamicRollingAverage;
import cc.funkemunky.anticheat.api.utils.Packets;
import cc.funkemunky.anticheat.api.utils.Setting;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

@BukkitEvents(events = {PlayerMoveEvent.class})
public class ReachB extends Check {
    @Setting(name = "vl.threshold")
    private double maxVL = 8.0;
    @Setting(name = "vl.deduct")
    private double deductVL = 0.5;
    @Setting(name = "vl.add.likely")
    private double likelyAdd = 0.5;
    @Setting(name = "vl.add.max")
    private double maxAdd = 1;
    @Setting(name = "threshold.max")
    private double maxReach = 3.5;
    @Setting(name = "threshold.likely")
    private double likelyReach = 3.33;
    @Setting(name = "averageSize")
    private int averageSize = 4;

    private DynamicRollingAverage reachAvg = new DynamicRollingAverage(averageSize);
    private double vl;

    public ReachB(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {

    }

    @Override
    public void onBukkitEvent(Event event) {
        val player = getData().getPlayer();
        val to = getData().getEntityTo();
        val from = getData().getEntityFrom();
        val target = getData().getTarget();
        if(to == null || from == null || getData().isGeneralCancel() || target == null || getData().getLastAttack().hasPassed(2)) return;

        double var9 = to.getX() - player.getLocation().getX();
        double var7;

        double mx = Math.cos(Math.toRadians(to.getYaw() + 90.0F));
        double mz = Math.sin(Math.toRadians(to.getYaw() + 90.0F));

        double x = (1 * 1 * mx + 0 * 1 * mz);
        double z = (1 * 1 * mz - 0 * 1 * mx);

        if (!(target instanceof Player) || !((Player) target).isSprinting()) {
            val dXX = Math.abs((from.getX() - x) - (player.getLocation().getX()));
            val dZZ = Math.abs((from.getZ() - z) - (player.getLocation().getZ()));

            val dX = Math.abs(from.getX() - player.getLocation().getX());
            val dZ = Math.abs(from.getZ() - player.getLocation().getZ());

            if (dXX + dZZ < dX + dZ) {
                return;
            }
        }

        // calculate real-time velocity
        for (var7 = to.getZ() - player.getLocation().getZ(); var9 * var9 + var7 * var7 < 1.0E-4D; var7 = (Math.random() - Math.random()) * 0.01D) {
            var9 = (Math.random() - Math.random()) * 0.01D;
        }

        double motionX = getData().getPlayer().getVelocity().getX() / 2.0;
        double motionY = getData().getPlayer().getVelocity().getY() / 2.0;
        double motionZ = getData().getPlayer().getVelocity().getZ() / 2.0;

        float var71 = (float) Math.sqrt(var9 * var9 + var7 * var7);
        float var8 = 0.4F;

        motionX -= var7 / (double) var71 * (double) var8;
        motionY += (double) var8;
        motionZ -= var9 / var7 * (double) var8;

        if (motionY > 0.4000000059604645D) {
            motionY = 0.4000000059604645D;
        }

        val distance = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);

        val dX = to.getX() - player.getLocation().getX();
        val dZ = to.getZ() - player.getLocation().getZ();

        val reachDistance = Math.sqrt(dX * dX + dZ * dZ - distance) - 0.25f;

        if (!target.isOnGround()) {
            return;
        }

        reachAvg.add(reachDistance);

        val reachAverage = reachAvg.getAverage();

        if (reachAverage > likelyReach) {
            if ((vl+= reachAverage > maxReach ? maxAdd : likelyAdd) > maxVL) {
                flag(reachAverage + ">-" + maxReach, true, true);
            }
        } else {
            vl -= vl > 0 ? deductVL : 0;
        }

        debug(reachAverage + ", " + vl);

        if (reachAvg.isReachedSize()) {
            debug("CLEARED");
            reachAvg.clearValues();
        }
    }
}