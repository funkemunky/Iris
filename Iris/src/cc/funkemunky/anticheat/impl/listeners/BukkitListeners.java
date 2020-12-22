package cc.funkemunky.anticheat.impl.listeners;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.checks.CancelType;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.Init;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;

@Init
public class BukkitListeners implements Listener {

    @EventHandler
    public void onEvent(PlayerMoveEvent event) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if (data != null && data.getLastMovementCancel().hasPassed(1)) {
            callChecks(data, event);

            if (data.getCancelType().equals(CancelType.MOTION)) {
                if(data.getSetbackLocation() != null) {
                    event.getPlayer().teleport(data.getSetbackLocation());
                } else {
                    event.setCancelled(true);
                }
                data.getLastServerPos().reset();
                data.setCancelType(CancelType.NONE);
            } else if (data.getMovementProcessor().isOnGround(data.getBoundingBox().shrink(0.1f,0,0.1f), data, 0.00001f) && data.getLastFlag().hasPassed(20)) {
                data.setSetbackLocation(event.getTo());
            }
        }
    }

    @EventHandler
    public void onEvent(BlockBreakEvent event) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if (data != null) {
            callChecks(data, event);

            if (data.getCancelType().equals(CancelType.BREAK)) {
                event.setCancelled(true);
                data.setCancelType(CancelType.NONE);
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerRespawnEvent event) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if(data != null) {
            data.getLastLogin().reset();
        }
    }

    @EventHandler
    public void onEvent(BlockPlaceEvent event) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if (data != null) {
            if(event.getBlockPlaced() != null && event.getBlockPlaced().getType().isSolid()) {
                data.getLastBlockPlace().reset();
            }
            callChecks(data, event);

            if (data.getCancelType().equals(CancelType.PLACE)) {
                event.setCancelled(true);
                data.setCancelType(CancelType.NONE);
            }
        }
    }

    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getPlayer().getUniqueId());

        if (data != null) {
            callChecks(data, event);

            if (data.getCancelType().equals(CancelType.INTERACT)) {
                event.setCancelled(true);
                data.setCancelType(CancelType.NONE);
            }

            if (event.getClickedBlock() != null && event.getItem() != null && event.getItem().getType().equals(Material.BLAZE_ROD) && event.getItem().getItemMeta().getDisplayName().equals(Color.Gold + "Magic Box Wand")) {
                List<BoundingBox> boxes = Atlas.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(event.getClickedBlock().getLocation());

                event.getPlayer().sendMessage(event.getClickedBlock().getType().toString() + ": " + (boxes.size() > 0 ? boxes.get(0).toString() : "0"));
                boxes.forEach(box -> cc.funkemunky.api.utils.MiscUtils.createParticlesForBoundingBox(event.getPlayer(), box));
            }
        }
    }

    @EventHandler
    public void onEvent(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            PlayerData data = Iris.getInstance().getDataManager().getPlayerData(player.getUniqueId());

            if (data != null) {
                callChecks(data, event);

                if(data.getCancelType() == CancelType.PROJECTILE) {
                    event.setCancelled(true);
                    data.setCancelType(CancelType.NONE);
                }
            }
        }
    }

    @EventHandler
    public void onEvent(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            PlayerData data = Iris.getInstance().getDataManager().getPlayerData(event.getEntity().getUniqueId());

            if (data != null) {
                callChecks(data, event);

                if (data.getCancelType().equals(CancelType.HEALTH)) {
                    event.setCancelled(true);

                    data.setCancelType(CancelType.NONE);
                }
            }
        }
    }

    private void callChecks(PlayerData data, Event event) {
        Atlas.getInstance().getThreadPool().execute(() -> data.getBukkitChecks().getOrDefault(event.getClass(), new ArrayList<>()).stream().filter(Check::isEnabled)
                .forEach(check -> {
                    Iris.getInstance().getProfiler().start("check:" + check.getName());
                    check.onBukkitEvent(event);
                    Iris.getInstance().getProfiler().stop("check:" + check.getName());
                }));
    }
}
