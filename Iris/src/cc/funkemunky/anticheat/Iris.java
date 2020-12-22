package cc.funkemunky.anticheat;

import cc.funkemunky.anticheat.api.checks.Check;
import cc.funkemunky.anticheat.api.checks.CheckManager;
import cc.funkemunky.anticheat.api.data.DataManager;
import cc.funkemunky.anticheat.api.data.logging.LoggerManager;
import cc.funkemunky.anticheat.api.data.stats.StatsManager;
import cc.funkemunky.anticheat.api.event.TickEvent;
import cc.funkemunky.anticheat.api.metrics.Metrics;
import cc.funkemunky.anticheat.api.utils.Message;
import cc.funkemunky.anticheat.api.utils.updater.Updater;
import cc.funkemunky.anticheat.impl.commands.iris.IrisCommand;
import cc.funkemunky.anticheat.impl.listeners.FunkeListeners;
import cc.funkemunky.anticheat.impl.listeners.PacketListeners;
import cc.funkemunky.anticheat.impl.menu.MenuUtils;
import cc.funkemunky.anticheat.impl.utils.IrisUpdater;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.utils.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

@Getter
public class Iris extends JavaPlugin {
    @Getter
    private static Iris instance;
    private DataManager dataManager;
    private CheckManager checkManager;
    private StatsManager statsManager;
    private IrisUpdater irisUpdater;
    private int currentTicks;
    private long lastTick, tickElapsed, profileStart;
    private ScheduledExecutorService executorService;
    private BaseProfiler profiler;
    private Metrics metrics;
    private LoggerManager loggerManager;
    private File messagesFile;
    private FileConfiguration messages;

    private String[] requiredVersionsOfAtlas = new String[] {"1.1.4.1"};

    @Override
    public void onEnable() {
        //This allows us to access this class's contents from others places.
        instance = this;

        saveDefaultConfig();
        createMessages();

        if(Bukkit.getPluginManager().isPluginEnabled("Atlas") && Arrays.stream(requiredVersionsOfAtlas).anyMatch(version -> Bukkit.getPluginManager().getPlugin("Atlas").getDescription().getVersion().equals(version))) {

            profiler = new BaseProfiler();
            profileStart = System.currentTimeMillis();

            //Starting up our utilities, managers, and tasks.
            checkManager = new CheckManager();
            dataManager = new DataManager();
            irisUpdater = new IrisUpdater();

            startScanner();
            checkForUpdates();

            if (!checkUpdater()) {
                MiscUtils.printToConsole("&c&lInvalid version of Iris, please update at https://www.spigotmc.org/resources/iris-anticheat-advanced-cheat-detection-1-7-1-13.53721/");
                Bukkit.getServer().shutdown();
                return;
            } else {
                MiscUtils.printToConsole("&aValid license, enjoy Iris!");
            }

            statsManager = new StatsManager();
            loggerManager = new LoggerManager();
            loggerManager.loadFromDatabase();

            runTasks();
            registerCommands();

            metrics = new Metrics(this);

            metrics.addCustomChart(new Metrics.SingleLineChart("alerts_sent", () -> getStatsManager().getFlagged()));
            metrics.addCustomChart(new Metrics.SingleLineChart("cheaters_removed", () -> (int) getStatsManager().getBanned()));
            metrics.addCustomChart(new Metrics.SimplePie("checks_enabled", () -> String.valueOf(checkManager.getChecks().stream().filter(Check::isEnabled).count())));
            metrics.addCustomChart(new Metrics.SimplePie("executable_checks", () -> String.valueOf(checkManager.getChecks().stream().filter(Check::isExecutable).count())));
            metrics.addCustomChart(new Metrics.SimplePie("checks_executable", () -> String.valueOf(checkManager.getChecks().stream().filter(Check::isExecutable).count())));
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "You do not the required Atlas dependency installed! You must download Atlas v" + requiredVersionsOfAtlas[0] + " for Iris to work properly.");
            if(getConfig().getBoolean("dependencies.Atlas.autoDownload")) {
                Bukkit.getLogger().log(Level.INFO, "Downloading the appropriate version of Atlas now...");
                Updater.downloadAppropriateVersion();
                Bukkit.getLogger().log(Level.INFO, "Download complete! Please restart your server.");
            } else {
                Bukkit.getLogger().log(Level.INFO, "You can turn set the dependencies.Atlas.autoDownload setting to true in the config to auto-magically download the proper version.");
            }
        }

        executorService = Executors.newSingleThreadScheduledExecutor();

        //Registering all the commands
    }

    public void onDisable() {
        statsManager.saveStats();
        loggerManager.saveToDatabase();
        EventManager.unregister(new FunkeListeners());
        EventManager.unregister(new PacketListeners());
        org.bukkit.event.HandlerList.unregisterAll(this);
        dataManager.getDataObjects().clear();
        checkManager.getChecks().clear();
        Bukkit.getScheduler().cancelTasks(this);
        executorService.shutdownNow();
    }

    private void runTasks() {
        //This allows us to use ticks for time comparisons to allow for more parrallel calculations to actual Minecraft
        //and it also has the added benefit of being lighter than using System.currentTimeMillis.
        new BukkitRunnable() {
            public void run() {
                TickEvent tickEvent = new TickEvent(currentTicks++);

                EventManager.callEvent(tickEvent);
            }
        }.runTaskTimerAsynchronously(this, 1L, 1L);

        new BukkitRunnable() {
            public void run() {
                long timeStamp = System.currentTimeMillis();
                tickElapsed = timeStamp - lastTick;
                //Bukkit.broadcastMessage(tickElapsed + "ms" + ", " + getTPS());
                lastTick = timeStamp;
            }
        }.runTaskTimer(Iris.getInstance(), 0L, 1L);
    }

    public void startScanner() {
        initializeScanner(getClass(), this);
    }

    private void registerCommands() {
        Atlas.getInstance().getFunkeCommandManager().addCommand(new IrisCommand());
    }

    public double getTPS() {
        return Bukkit.getServer().getVersion().toLowerCase().contains("paper") ? ReflectionsUtil.getTPS(Bukkit.getServer()) : 1000D / tickElapsed;
    }

    public double getTPS(RoundingMode mode, int places) {
        return MathUtils.round(getTPS(), places, mode);
    }

    private boolean checkUpdater() {
        boolean enabled = true;
        try {
            URL url = new URL("https://pastebin.com/raw/265uYkmM");
            Scanner scanner = new Scanner(url.openStream());
            enabled = Boolean.valueOf(scanner.nextLine());
            scanner.close();
        } catch (Exception ignored) {}
        return enabled;

    }

    private void checkForUpdates() {
        IrisUpdater irisUpdater = Iris.getInstance().getIrisUpdater();
        try {
            if (irisUpdater.checkForUpdates()) {
                MiscUtils.printToConsole("&cA new update for Iris was found! v" + irisUpdater.getNewVersion() + " - Download it at https://www.spigotmc.org/resources/iris-anticheat-advanced-cheat-detection-1-7-1-13.53721/");
            } else {
                MiscUtils.printToConsole("&aIris is up to date.");
            }
        } catch (Exception e) {
            MiscUtils.printToConsole("&cUnable to check for Iris updates.");
        }
    }

    public void reloadIris() {
        Atlas.getInstance().getThreadPool().execute(() -> {
            cc.funkemunky.anticheat.api.utils.MiscUtils.unloadPlugin("Iris");
            cc.funkemunky.anticheat.api.utils.MiscUtils.loadPlugin("Iris");
        });
    }

    public void reloadIris(Player player) {
        Atlas.getInstance().getThreadPool().execute(() -> {
            cc.funkemunky.anticheat.api.utils.MiscUtils.unloadPlugin("Iris");
            cc.funkemunky.anticheat.api.utils.MiscUtils.loadPlugin("Iris");
            new BukkitRunnable() {
                public void run() {
                    MenuUtils.openCheckEditGUI(player);
                }
            }.runTask(Iris.getInstance());
        });
    }

    public FileConfiguration getMessages() {
        if (messages == null) {
            reloadMessages();
        }
        return messages;
    }


    public void saveMessages() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            getMessages().save(messagesFile);
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save config to " + messagesFile, ex);
        }
    }

    public void createMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        // Look for defaults in the jar
        try {
            Reader defConfigStream = new InputStreamReader(this.getResource("messages.yml"), "UTF8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            messages.setDefaults(defConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeScanner(Class<?> mainClass, Plugin plugin) {
        ClassScanner.scanFile(null, mainClass).stream().filter(c -> {
            try {
                Class clazz = Class.forName(c);

                return clazz.isAnnotationPresent(Init.class);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return false;
        }).sorted(Comparator.comparingInt(c -> {
            try {
                Class clazz = Class.forName(c);

                Init annotation = (Init) clazz.getAnnotation(Init.class);

                return annotation.priority().getPriority();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return 3;
        })).forEachOrdered(c -> {
            try {
                Class clazz = Class.forName(c);

                if(clazz.isAnnotationPresent(Init.class)) {
                    Object obj = clazz.getSimpleName().equals(mainClass.getSimpleName()) ? plugin : clazz.newInstance();
                    Init init = (Init) clazz.getAnnotation(Init.class);
                    if (obj instanceof Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Bukkit listener. Registering...");
                        Bukkit.getPluginManager().registerEvents((Listener) obj, plugin);
                    } else if(obj instanceof cc.funkemunky.api.event.system.Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Atlas listener. Registering...");
                        EventManager.register(plugin, (cc.funkemunky.api.event.system.Listener) obj);
                    }

                    if(init.commands()) {
                        Atlas.getInstance().getCommandManager().registerCommands(obj);
                    }

                    for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        if(field.isAnnotationPresent(ConfigSetting.class)) {
                            String name = field.getAnnotation(ConfigSetting.class).name();
                            String path = field.getAnnotation(ConfigSetting.class).path() + "." + (name.length() > 0 ? name : field.getName());
                            try {
                                MiscUtils.printToConsole("&eFound " + field.getName() + " ConfigSetting (default=" + field.get(obj) + ").");
                                if(plugin.getConfig().get(path) == null) {
                                    MiscUtils.printToConsole("&eValue not found in configuration! Setting default into config...");
                                    plugin.getConfig().set(path, field.get(obj));
                                    plugin.saveConfig();
                                } else {
                                    field.set(obj, plugin.getConfig().get(path));

                                    MiscUtils.printToConsole("&eValue found in configuration! Set value to &a" + plugin.getConfig().get(path));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if(field.isAnnotationPresent(Message.class)) {
                            String name = field.getAnnotation(Message.class).name();
                            String path = field.getAnnotation(Message.class).path() + "." + (name.length() > 0 ? name : field.getName());
                            try {
                                field.setAccessible(true);
                                MiscUtils.printToConsole("&eFound " + field.getName() + " message (default=" + field.get(obj) + ").");
                                if(getMessages().get(path) == null) {
                                    MiscUtils.printToConsole("&eValue not found in messages.yml! Setting default into the config...");
                                    getMessages().set(path, field.get(obj));
                                    saveMessages();
                                } else {
                                    field.set(obj, getMessages().get(path));

                                    MiscUtils.printToConsole("&eValue found in message.yml! Set value to &a" + getMessages().get(path));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
