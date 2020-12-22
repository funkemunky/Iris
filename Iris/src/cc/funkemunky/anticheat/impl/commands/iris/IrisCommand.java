package cc.funkemunky.anticheat.impl.commands.iris;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.anticheat.impl.commands.iris.arguments.*;
import cc.funkemunky.api.commands.CommandMessages;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;

public class IrisCommand extends FunkeCommand {
    public IrisCommand() {
        super(Iris.getInstance(), "iris", "Iris", "The Iris anticheat main command.", "iris.command");
        setAdminPerm("iris.*");
    }

    @Override
    protected void addArguments() {
        setCommandMessages(new CommandMessages(
                Color.translate(Messages.noPermission),
                Color.translate(Messages.invalidArguments),
                Color.translate(Messages.playerOnly),
                Color.translate(Messages.consoleOnly),
                Color.translate(Messages.primaryColor),
                Color.translate(Messages.secondaryColor),
                Color.translate(Messages.titleColor),
                Color.translate(Messages.errorColor),
                Color.translate(Messages.valueColor),
                Color.translate(Messages.successColor)));
        getArguments().add(new ReloadArgument(this, "reload", "reload", "reload the Iris config.", "iris.reload"));
        getArguments().add(new LagArgument(this, "lag", "lag <profile,server,player> [args]", "view extensive lag information.", "iris.lag"));
        getArguments().add(new MenuArgument(this, "menu", "menu", "open check editor.", "iris.menu"));
        getArguments().add(new AlertsArgument(this, "alerts", "alerts", "toggle your alerts", "iris.alerts"));
        getArguments().add(new LogArgument(this, "logs", "logs <player>", "view the logs of a player", "iris.logs"));
        getArguments().add(new BugReportArg(this, "bugreport", "bugreport <config,info>", "use when making a bug report.", "iris.bugreport"));
        getArguments().add(new DebugArgument(this, "debug", "debug <check,none> [player]", "debug a check.", "iris.debug"));
        getArguments().add(new SaveArgument(this, "save", "save", "save all data", "iris.save"));
        getArguments().add(new BoxWandArgument(this, "boxwand", "boxwand", "receive the magic box wand.", "iris.boxwand"));
        getArguments().add(new BroadcastArg(this, "broadcast", "broadcast <msg>", "broadcast a message from console only."));
    }
}
