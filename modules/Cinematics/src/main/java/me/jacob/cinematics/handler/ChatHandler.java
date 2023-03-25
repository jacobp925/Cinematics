package me.jacob.cinematics.handler;

import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import org.bukkit.entity.Player;

public class ChatHandler {

    public void inject(Player player, CinematicPlayer cinematicPlayer) {
        if (!Cinematics.getInstance().getCinematicHandler().isDisableChat()) return;
        Cinematics.getInstance().getNmsVersion().inject(player, cinematicPlayer);
    }
}
