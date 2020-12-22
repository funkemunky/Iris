package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.anticheat.impl.menu.MenuUtils;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuArgument extends FunkeArgument {
    public MenuArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);

        setPlayerOnly(true);
    }

    @Override
    public void onArgument(CommandSender commandSender, Command command, String[] strings) {
        Player player = (Player) commandSender;

        MenuUtils.openMainGUI(player);
        player.sendMessage(Color.translate(Messages.successColor + Messages.openedMenu));
    }
}
