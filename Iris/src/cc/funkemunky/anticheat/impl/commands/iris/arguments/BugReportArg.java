package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.anticheat.api.utils.Pastebin;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BugReportArg extends FunkeArgument {


    public BugReportArg(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if(args.length > 1) {
            switch(args[1].toLowerCase()) {
                case "config": {
                    Atlas.getInstance().getThreadPool().execute(() -> {
                        try {
                            sender.sendMessage(Color.translate(Messages.successColor) + "Config Paste: " + Pastebin.makePaste(config(Iris.getInstance()).toString(), Iris.getInstance().getServer().getName() + " Iris Config", Pastebin.Privacy.UNLISTED));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                }
                case "report":
                case "info": {
                    Atlas.getInstance().getThreadPool().execute(() -> {
                        try {
                            sender.sendMessage(Color.translate(Messages.successColor) + "Config Paste: " + Pastebin.makePaste(latestLog(Iris.getInstance()).toString(), Iris.getInstance().getServer().getName() + " Iris Config", Pastebin.Privacy.UNLISTED));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                }
                default: {


                    break;
                }
            }
        }
    }

    private StringBuilder config(Plugin plugin)
    {
        BufferedReader br = null;
        StringBuilder txt = new StringBuilder();
        try {
            String line;
            br = new BufferedReader(new FileReader("" + plugin.getDataFolder() + File.separatorChar + "config.yml"));
            while ((line = br.readLine()) != null) {
                txt.append(line);
                txt.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return txt;
    }

    private StringBuilder latestLog(Plugin plugin)
    {
        BufferedReader br = null;
        StringBuilder txt = new StringBuilder();
        try {
            String line;
            val irisVersion = Iris.getInstance().getDescription().getVersion();
            val atlasVersion = Iris.getInstance().getServer().getPluginManager().getPlugin("Atlas").getDescription().getVersion();

            List<String> body = new ArrayList<>();
            body.add(MiscUtils.lineNoStrike());
            float totalPCT = 0;
            long totalTime = MathUtils.elapsed(Iris.getInstance().getProfileStart());
            for (String string : Iris.getInstance().getProfiler().total.keySet()) {
                body.add(string);
                double stringTotal = TimeUnit.NANOSECONDS.toMillis(Iris.getInstance().getProfiler().total.get(string));
                int calls = Iris.getInstance().getProfiler().calls.get(string);
                double pct = stringTotal / totalTime;
                body.add("Latency: " + stringTotal / calls + "ms");
                body.add("Calls: " + calls);
                body.add("STD: " + Iris.getInstance().getProfiler().stddev.get(string));
                body.add("PCT: " + MathUtils.round(pct, 8));
                totalPCT += (pct);
            }
            body.add("Total PCT: " + MathUtils.round(totalPCT, 4) + "%");
            body.add("Total Time: " + totalTime + "ms");
            body.add("Total Calls: " + Iris.getInstance().getProfiler().totalCalls);
            body.add(MiscUtils.lineNoStrike());

            StringBuilder builder = new StringBuilder();
            for (String aBody : body) {
                builder.append(aBody).append(";");
            }

            builder.deleteCharAt(body.size() - 1);

            String bodyString = builder.toString().replaceAll(";", "\n");
            txt.append("###########################################")
                    .append("\n")
                    .append("Iris Information Log")
                    .append("\n")
                    .append("###########################################")
                    .append("\n")
                    .append("\n")
                    .append("Iris Version: " + irisVersion)
                    .append("\n")
                    .append("Atlas Version: " + atlasVersion)
                    .append("\n")
                    .append("Online Players: " + Bukkit.getOnlinePlayers().size())
                    .append("\n")
                    .append("\n")
                    .append("Profile:")
                    .append("\n")
                    .append(bodyString)
                    .append("\n")
                    .append("\n")
                    .append("Plugins: ");

            for (int i = 0; i < Iris.getInstance().getServer().getPluginManager().getPlugins().length; i++) {
                Plugin pl = Iris.getInstance().getServer().getPluginManager().getPlugins()[i];

                txt.append(pl.getName() + (i + 1 < Iris.getInstance().getServer().getPluginManager().getPlugins().length ? ", " : ""));
            }

            txt.append("\n").append("\n").append("Latest Log:")
                    .append("\n");

            br = new BufferedReader(new FileReader("logs" + File.separatorChar + "latest.log"));
            while ((line = br.readLine()) != null) {
                txt.append(line);
                txt.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return txt;
    }
}
