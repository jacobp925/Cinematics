package me.jacob.cinematics.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player enters a cinematic
 */
public class CinematicStartEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;

    public CinematicStartEvent(@NotNull final Player player) {
        super(player);
        this.cancel = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}