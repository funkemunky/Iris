package cc.funkemunky.anticheat.impl.checks.player;

import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.BukkitEvents;
import cc.funkemunky.anticheat.api.utils.Verbose;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.math.RayTrace;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

/**
 * Created by George on 09/03/2019 at 10:27.
 */
@BukkitEvents(events = {InventoryOpenEvent.class})
public class ChestAura extends Check {

    public ChestAura(String name, CancelType cancelType, int maxVL) {
        super(name, cancelType, maxVL);
    }

    private Verbose verbose = new Verbose();
    private long lastInvClick;
    private long lastElapsed;

    @Override
    public void onPacket(Object packet, String packetType, long timeStamp) {

    }

    @Override
    public void onBukkitEvent(Event event) {
        InventoryOpenEvent e = (InventoryOpenEvent) event;

        // Cancel out all GUIs
        if (!ChatColor.stripColor(e.getInventory().getTitle()).equals("Chest")) {
            return;
        }

        RayTrace rayTrace = new RayTrace(getData().getPlayer().getEyeLocation().toVector(), getData().getPlayer().getEyeLocation().getDirection());

        List<Vector> vectors = rayTrace.traverse(4.5, 0.25);
        Optional<Vector> optional = vectors.stream().filter(vector -> BlockUtils.isSolid(new Location(e.getPlayer().getWorld(), vector.getX(), vector.getY(), vector.getZ()).getBlock())).findFirst();

        if (optional.isPresent()) {
            Vector vector = optional.get();
            Block block = new Location(e.getPlayer().getWorld(), vector.getX(), vector.getY(), vector.getZ()).getBlock();
            if (!BlockUtils.isChest(block)) {
                flag("", true, true);
            }
        } else {
            flag("", true, true);
        }
    }
}
