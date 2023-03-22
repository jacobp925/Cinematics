package me.jacob.cinematics.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player exits all cinematics
 */
public class CinematicEndEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public CinematicEndEvent(@NotNull final Player player) {
        super(player);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}