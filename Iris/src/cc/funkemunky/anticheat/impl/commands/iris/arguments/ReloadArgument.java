package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadArgument extends FunkeArgument {
    public ReloadArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);
    }

    @Override
    public void onArgument(CommandSender commandSender, Command command, String[] strings) {
        commandSender.sendMessage(Color.translate(Messages.primaryColor + Messages.startReloading));
        Iris.getInstance().reloadIris();
        commandSender.sendMessage(Color.translate(Messages.successColor + Messages.completedReloading));
    }
}
