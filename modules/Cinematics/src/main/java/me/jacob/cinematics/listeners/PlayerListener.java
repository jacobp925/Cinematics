package me.jacob.cinematics.listeners;

import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import me.jacob.cinematics.objects.cinematics.LinearCinematic;
import me.jacob.cinematics.objects.cinematics.SplineCinematic;
import me.jacob.cinematics.util.ColorUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final Cinematics plugin;

    public PlayerListener() {
        this.plugin = Cinematics.getInstance();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getChatHandler().inject(event.getPlayer(), new CinematicPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        CinematicPlayer cinematicPlayer = CinematicPlayer.getByUUID(event.getPlayer().getUniqueId());
        if (cinematicPlayer == null) return;

        if (cinematicPlayer.isInCinematic()) cinematicPlayer.exitCinematic();

        cinematicPlayer.remove();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();

        CinematicPlayer player = CinematicPlayer.getByUUID(p.getUniqueId());
        if (player == null) return;

        if (!player.isCreatingCinematic()) return;

        if (event.getMessage().equalsIgnoreCase("add")) {

            player.getWaypoints().add(p.getLocation());
            p.sendMessage(ColorUtil.color("&aAdded point " + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getLocation().getZ()));
            event.setCancelled(true);

        } else if (event.getMessage().startsWith("end")) {

            boolean spline = false;
            int speed = 1;
            int worldTime = 5000;
            double smoothness = 0.1;

            String[] parts = event.getMessage().split(" ");
            if (parts.length != 1) {

                if (parts.length > 1) {
                    spline = Boolean.parseBoolean(parts[1]);
                }
                if (parts.length > 2) {
                    try {
                        speed = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException ex) {
                        p.sendMessage(ColorUtil.color("&cInvalid number for speed. Must be a integer."));
                        return;
                    }
                }
                if (parts.length > 3) {
                    try {
                        worldTime = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException ex) {
                        p.sendMessage(ColorUtil.color("&cInvalid number for world time. Must be a integer."));
                        return;
                    }
                }
                if (parts.length > 4) {
                    try {
                        smoothness = Double.parseDouble(parts[3]);
                    } catch (NumberFormatException ex) {
                        p.sendMessage(ColorUtil.color("&cInvalid number for smoothness. Must be a double."));
                        return;
                    }
                }
            }

            if (spline) {
                new SplineCinematic(player.getCreatingCinematicName(), speed, worldTime, player.getWaypoints(), true);
            } else {
                new LinearCinematic(player.getCreatingCinematicName(), speed, worldTime, smoothness, player.getWaypoints(), true);
            }
            p.sendMessage(ColorUtil.color("&aCreated new " + (spline ? "spline " : "") + "cinematic " + player.getCreatingCinematicName() + " with " + player.getWaypoints().size() + " points, speed " + speed + ", world time " + worldTime + ", and smoothness " + smoothness));
            player.endCinematicCreation();
            event.setCancelled(true);

        } else if (event.getMessage().equalsIgnoreCase("cancel")) {

            p.sendMessage(ChatColor.RED + "Cancelled cinematic creation");
            player.endCinematicCreation();
            event.setCancelled(true);

        }
    }

}
