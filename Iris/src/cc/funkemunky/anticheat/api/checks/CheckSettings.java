package cc.funkemunky.anticheat.api.checks;

import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;

import java.util.Collections;
import java.util.List;

@Init(priority = Priority.HIGHEST)
public class CheckSettings {
    @ConfigSetting(name = "executableCommand")
    static List<String> executableCommand = Collections.singletonList("kick %player% [IRIS] You have been kicked for failing %check%");

    @ConfigSetting(name = "key")
    public static String key = "";

    @ConfigSetting(path = "alerts", name = "alertMessage")
    static String alertMessage = Messages.prefix + " &f%player% &7has failed &f%check% &c(x%vl%)";

    @ConfigSetting(path = "alerts", name = "alertsDelay")
    static long alertsDelay = 1000;

    @ConfigSetting(path = "alerts", name = "testMode")
    public static boolean testMode = false;

    @ConfigSetting(path = "alerts", name = "printToConsole")
    static boolean printToConsole = false;

    @ConfigSetting(path = "alerts", name = "enableOnJoin")
    public static boolean enableOnJoin = true;
}
