package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.data.PlayerData;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsArgument extends FunkeArgument {

    public AlertsArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);

        addAlias("toggleAlerts");
        addAlias("ta");

        setPlayerOnly(true);
    }

    @Override
    public void onArgument(CommandSender sender, Command command, String[] args) {
        PlayerData data = Iris.getInstance().getDataManager().getPlayerData(((Player) sender).getUniqueId());

        if(data == null) {
            sender.sendMessage(Color.translate(Messages.errorColor + Messages.dataNotFound));
            return;
        }

        data.setAlertsEnabled(!data.isAlertsEnabled());
        sender.sendMessage(Color.translate(data.isAlertsEnabled() ? Messages.alertsOn : Messages.alertsOff));
    }
}
