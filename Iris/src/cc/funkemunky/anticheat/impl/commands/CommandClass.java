package cc.funkemunky.anticheat.impl.commands;

import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.*;

@Init(commands = true)
public class CommandClass {

    @Command(name = "entitybox", playerOnly = true)
    public void onCommand(CommandAdapter command) {
        BoundingBox one = MiscUtils.getEntityBoundingBox(command.getPlayer());
        BoundingBox two = ReflectionsUtil.toBoundingBox(ReflectionsUtil.getBoundingBox(command.getPlayer()));

        command.getSender().sendMessage(Color.Red + one.toString() + Color.Gray + "; " + Color.Blue + two.toString());
    }
}
