package cc.funkemunky.anticheat.api.utils;

import cc.funkemunky.api.utils.Init;

@Init
public class Messages {

    @Message(name = "prefix")
    public static String prefix = "&8[&b&lIris&8]";

    @Message(path = "color", name = "error")
    public static String errorColor = "&c";

    @Message(path = "color", name = "primary")
    public static String primaryColor = "&7";

    @Message(path = "color", name = "secondary")
    public static String secondaryColor = "&b";

    @Message(path = "color", name = "title")
    public static String titleColor = "&9&l";

    @Message(path = "color", name = "value")
    public static String valueColor = "&f";

    @Message(path = "color", name = "success")
    public static String successColor = "&a";

    @Message(path = "commands.error", name = "noPermission")
    public static String noPermission = "No permission.";

    @Message(path = "commands.error", name = "invalidArguments")
    public static String invalidArguments = "Invalid arguments. Check the Iris help page for more information.";

    @Message(path = "commands.error", name = "playerOnly")
    public static String playerOnly = "This command is for players only.";

    @Message(path = "commands.error", name = "playerNotFound")
    public static String playerNotFound = "Could not find player.";

    @Message(path = "commands.error", name = "dataNotFound")
    public static String dataNotFound = "There was an error trying to find this specified user's data object.";

    @Message(path = "commands.error", name = "consoleOnly")
    public static String consoleOnly = "This command is for console only.";

    @Message(path = "commands.save", name = "startSaving")
    public static String startSaving = "Saving all data...";

    @Message(path = "commands.save", name = "completed")
    public static String completedSaving = "Completed!";

    @Message(path = "commands.reload", name = "startReloading")
    public static String startReloading = "Fully reloading Iris...";

    @Message(path = "commands.reload", name = "completed")
    public static String completedReloading = "Completed!";

    @Message(path = "commands.menu", name = "openedMenu")
    public static String openedMenu = "Opened the menu.";

    @Message(path = "commands.boxWand", name = "success")
    public static String boxWandSuccess = "Gave you the magic box wand. Use it wisely.";

    @Message(path = "commands.alerts", name = "toggledOn")
    public static String alertsOn = "&7Toggled your alerts &aon&7.";

    @Message(path = "commands.alerts", name = "toggledOff")
    public static String alertsOff = "&7Toggled your alerts &coff&7.";
}
