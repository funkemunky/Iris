package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class BroadcastArg extends FunkeArgument {
    public BroadcastArg(FunkeCommand parent, String name, String display, String description) {
        super(parent, name, display, description);

        addAlias("bc");
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            StringBuilder builder = new StringBuilder();

            if(args.length > 1) {
                for(int i = 1 ; i < args.length ; i++) builder.append(args[i]).append(";");

                builder.deleteCharAt(args.length - 1);

                Bukkit.broadcastMessage(Color.translate(builder.toString().replaceAll(";", " ")));
            } else {
                sender.sendMessage(getParent().getCommandMessages().getErrorColor() + getParent().getCommandMessages().getInvalidArguments());
            }
        }
    }
}
