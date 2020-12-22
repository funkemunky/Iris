package cc.funkemunky.anticheat.impl.listeners;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.impl.utils.IrisUpdater;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Init;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Init
public class PlayerConnectionListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Atlas.getInstance().getThreadPool().execute(() -> {
            Iris.getInstance().getDataManager().addData(event.getPlayer().getUniqueId());
            if(Iris.getInstance().getStatsManager().isPlayerBanned(event.getPlayer().getUniqueId())) {
                Iris.getInstance().getLoggerManager().removeBan(event.getPlayer().getUniqueId());
            }
        });
    }

    @EventHandler
    public void onPlayerJoinNormal(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            IrisUpdater irisUpdater = Iris.getInstance().getIrisUpdater();
            try {
                if (irisUpdater.checkForUpdates()) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4A new update for Iris was found! v" + irisUpdater.getNewVersion() + " - Download it at https://www.spigotmc.org/resources/iris-anticheat-advanced-cheat-detection-1-7-1-13.53721/"));
                }
            } catch (Exception e) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUnable to check for Iris updates."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Iris.getInstance().getDataManager().removeData(event.getPlayer().getUniqueId());
    }
}
