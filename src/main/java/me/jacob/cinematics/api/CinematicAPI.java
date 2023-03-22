package me.jacob.cinematics.api;

import me.jacob.cinematics.objects.CinematicPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CinematicAPI {

    /**
     * Play a list of cinematics in order
     *
     * @param player
     * @param cinematicNames
     */
    public static void playCinematic(@NotNull Player player, @NotNull String... cinematicNames) {
        CinematicPlayer cinematicPlayer = CinematicPlayer.getByUUID(player.getUniqueId());
        cinematicPlayer.playCinematics(List.of(cinematicNames));
    }

    /**
     * Check if player is in cinematic
     *
     * @param player
     * @return inCinematic
     */
    public static boolean isPlayerInCinematic(@NotNull Player player) {
        CinematicPlayer cinematicPlayer = CinematicPlayer.getByUUID(player.getUniqueId());
        return cinematicPlayer != null && cinematicPlayer.isInCinematic();
    }

}
