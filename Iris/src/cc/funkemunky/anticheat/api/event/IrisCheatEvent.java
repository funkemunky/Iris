package cc.funkemunky.anticheat.api.event;

import cc.funkemunky.anticheat.api.checks.Check;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class IrisCheatEvent extends Event implements Cancellable {
    @Getter
    private static boolean cancelled;
    @Getter
    private static HandlerList handlerList;
    private Player player;
    private Check check;
    private String information;
    private boolean cancellable, bannable;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        IrisCheatEvent.cancelled = cancelled;
    }
}
