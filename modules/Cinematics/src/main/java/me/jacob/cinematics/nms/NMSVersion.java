package me.jacob.cinematics.nms;

import me.jacob.cinematics.objects.CinematicPlayer;
import org.bukkit.entity.Player;

public interface NMSVersion {

    void inject(Player player, CinematicPlayer cinematicPlayer);

    void processChat(Player player, CinematicPlayer cinematicPlayer);

}