package cc.funkemunky.anticheat.impl.commands.iris.arguments;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.anticheat.api.utils.Messages;
import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.Init;
import lombok.val;

@Init(commands = true)
public class AlertsCommand {
    @Command(name = "alerts", permission = {"iris.*", "iris.alerts", "iris.staff"}, playerOnly = true)
    public void onCommand(CommandAdapter command) {
        val player = command.getPlayer();
        val data = Iris.getInstance().getDataManager().getPlayerData(player.getUniqueId());

        if(data == null) {
            player.sendMessage(Color.translate(Messages.errorColor + Messages.dataNotFound));
            return;
        }

        data.setAlertsEnabled(!data.isAlertsEnabled());
        player.sendMessage(Color.translate(data.isAlertsEnabled() ? Messages.alertsOn : Messages.alertsOff));
    }
}
