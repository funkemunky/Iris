package cc.funkemunky.anticheat.impl.menu;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.utils.menu.button.Button;
import cc.funkemunky.anticheat.api.utils.menu.button.ClickAction;
import cc.funkemunky.anticheat.api.utils.menu.type.impl.ChestMenu;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.LogRecord;

public class MenuUtils {
    public static boolean hasModifiedChecks = false;

    private static Button createButton(boolean moveable, ItemStack stack, ClickAction action) {
        return new Button(moveable, stack, action);
    }

    public static void openCheckEditGUI(Player toOpen) {
        ChestMenu menu = new ChestMenu(Color.Gold + "Edit Checks", 6);

        Iris.getInstance().getCheckManager().getChecks().forEach(check -> menu.addItem(checkButton(check)));

        if(hasModifiedChecks) {
            menu.setItem(menu.getMenuDimension().getSize() - 1, saveChangesButton());
        }

        menu.showMenu(toOpen);
    }

    public static void openLogGUI(Player toOpen, OfflinePlayer target) {
        ChestMenu menu = new ChestMenu(Color.Gold + target.getName() + "'s Logs", 6);

        Map<String, Integer> logs = Iris.getInstance().getLoggerManager().getViolations(target.getUniqueId());
        String banReason = Iris.getInstance().getLoggerManager().getBanReason(target.getUniqueId());
        logs.keySet().forEach(key -> {
            menu.addItem(createButton(false, MiscUtils.createItem(key.equalsIgnoreCase(banReason) ? Material.ENCHANTED_BOOK : Material.BOOK, 1, Color.Blue + key, "", "&bViolations&8: &f" + logs.get(key)), null));
        });

        menu.setItem(49, createButton(false, MiscUtils.createItem(Material.REDSTONE, 1, Color.Red + "Clear Logs"),
                ((player, infoPair) -> {
                    logs.clear();
                    Iris.getInstance().getLoggerManager().clearLogs(target.getUniqueId());
                    infoPair.getMenu().close(player);
                    openLogGUI(player, target);
                })));
        menu.showMenu(toOpen);
    }

    public static void openMainGUI(Player toOpen) {
        ChestMenu menu = new ChestMenu(Color.Gold + "Iris Menu", 3);

        menu.setItem(11, createButton(false, MiscUtils.createItem(Material.BOOK_AND_QUILL, 1, Color.Blue + "Edit Check Settings"), ((player2, informationPair) -> {
            if(informationPair.getClickType().toString().contains("LEFT")) {
                openCheckEditGUI(player2);
            }
        })));

        menu.setItem(
                13,mainButton());

        menu.setItem(15, createButton(false, MiscUtils.createItem(Material.WATCH, 1, Color.Blue + "Reload Iris"), ((player, infoPair) -> {
            infoPair.getMenu().close(player);
            player.sendMessage(Color.translate("&8[&b&lIris&8] &7Fully unloading and loading Iris..."));
            Iris.getInstance().reloadIris();
        })));

        menu.showMenu(toOpen);
    }

    private static Button mainButton() {
        return createButton(false, MiscUtils.createItem(Material.SIGN, 1, Color.Blue + "Iris Anticheat", "", "&7You are using &bv" + Iris.getInstance().getDescription().getVersion() + "&7.", "", "&bBanned&8: &f" + Iris.getInstance().getStatsManager().getBanned(), "&bFlagged&8: &f" + Iris.getInstance().getStatsManager().getFlagged() , "", "&7If you have any issues or questions, please", "&fLeft Click &7to get the link to our Support Discord.", "&fShift + Left Click &7to reset statistics."),
                ((player, infopair) -> {
                    switch(infopair.getClickType()) {
                        case LEFT:
                            player.sendMessage(Color.translate("&bOur Support Discord&8: &fhttp://discord.me/Iris"));
                            infopair.getMenu().close(player);
                            break;
                        case SHIFT_LEFT:
                            Iris.getInstance().getStatsManager().resetStats();
                            infopair.getMenu().setItem(13, createButton(false, MiscUtils.createItem(Material.SIGN, 1,Color.Green + "Successfully reset statistics!"), null));
                            infopair.getMenu().buildInventory(false);
                            new BukkitRunnable() {
                                public void run() {
                                    infopair.getMenu().setItem(13, mainButton());
                                    infopair.getMenu().buildInventory(false);
                                }
                            }.runTaskLater(Iris.getInstance(), 20L);
                            break;
                    }
                }));
    }

    private static Button checkButton(Check check) {
        return createButton(false,
                MiscUtils.createItem(
                        Material.PAPER,
                        1,
                        Color.Blue + check.getName(),
                        "",
                        "&bEnabled&7: &f" + check.isEnabled(),
                        "&bExecutable&7: &f" + check.isExecutable(),
                        "&bCancellable&7: &f" + check.isCancellable(),
                        "",
                        "&bInstructions&7:",
                        "&8- &fLeft Click &7to toggle check on/off.",
                        "&8- &fShift + Left Click &7to toggle check executable-abilities.",
                        "&8- &fRight Click &7to toggle check cancellable-abilities."),
                ((player2, infoPair) -> {
                    switch(infoPair.getClickType()) {
                        case LEFT:
                            Iris.getInstance().getConfig().set("checks." + check.getName() + ".enabled", !check.isEnabled());

                            check.setEnabled(!check.isEnabled());
                            for (int i = 0; i < Iris.getInstance().getCheckManager().getChecks().size(); i++) {
                                infoPair.getMenu().setItem(i, checkButton(Iris.getInstance().getCheckManager().getChecks().get(i)));
                            }
                            if(!hasModifiedChecks) {
                                infoPair.getMenu().setItem(infoPair.getMenu().getMenuDimension().getSize() - 1, saveChangesButton());
                                infoPair.getMenu().buildInventory(false);
                            }
                            infoPair.getMenu().buildInventory(false);
                            hasModifiedChecks = true;
                            break;
                        case SHIFT_LEFT:
                            Iris.getInstance().getConfig().set("checks." + check.getName() + ".executable", !check.isExecutable());
                            check.setExecutable(!check.isExecutable());
                            for (int i = 0; i < Iris.getInstance().getCheckManager().getChecks().size(); i++) {
                                infoPair.getMenu().setItem(i, checkButton(Iris.getInstance().getCheckManager().getChecks().get(i)));
                            }
                            if(!hasModifiedChecks) {
                                infoPair.getMenu().setItem(infoPair.getMenu().getMenuDimension().getSize() - 1, saveChangesButton());
                                infoPair.getMenu().buildInventory(false);
                            }
                            infoPair.getMenu().buildInventory(false);
                            hasModifiedChecks = true;
                            break;
                        case RIGHT:
                            Iris.getInstance().getConfig().set("checks." + check.getName() + ".cancellable", !check.isCancellable());
                            check.setCancellable(!check.isCancellable());
                            for (int i = 0; i < Iris.getInstance().getCheckManager().getChecks().size(); i++) {
                                infoPair.getMenu().setItem(i, checkButton(Iris.getInstance().getCheckManager().getChecks().get(i)));
                            }
                            if(!hasModifiedChecks) {
                                infoPair.getMenu().setItem(infoPair.getMenu().getMenuDimension().getSize() - 1, saveChangesButton());
                            }
                            infoPair.getMenu().buildInventory(false);
                            hasModifiedChecks = true;
                            break;
                    }
                }));
    }

    private static Button saveChangesButton() {
        return createButton(false, MiscUtils.createItem(Material.BOOK_AND_QUILL, 1, Color.Red + "Save Changes"), ((player2, infoPair) -> {
            hasModifiedChecks = false;
            Iris.getInstance().saveConfig();
            Iris.getInstance().getDataManager().getDataObjects().clear();
            Bukkit.getOnlinePlayers().forEach(player -> Iris.getInstance().getDataManager().addData(player.getUniqueId()));
            openCheckEditGUI(player2);
        }));
    }

}
