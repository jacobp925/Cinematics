package me.jacob.cinematics.handler;

import org.bukkit.entity.Player;

public class TitleHandler {

    public void playFade(Player p, int fadeIn, int stay, int fadeOut) {
        p.sendTitle("\uE000", "", fadeIn, stay, fadeOut);
    }
}
