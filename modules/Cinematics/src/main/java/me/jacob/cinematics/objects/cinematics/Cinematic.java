package me.jacob.cinematics.objects.cinematics;

import lombok.Getter;
import lombok.Setter;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.api.events.CinematicStartEvent;
import me.jacob.cinematics.objects.CinematicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

@Getter
@Setter
public abstract class Cinematic {

    @Getter private static HashMap<String, Cinematic> cinematics = new HashMap<>();

    private String name;
    private int speed;
    private int worldTime;
    private LinkedList<Location> waypoints;

    public Cinematic(String name, int speed, int worldTime, ArrayList<Location> waypoints) {
        this.name = name;
        this.speed = speed;
        this.worldTime = worldTime;
        this.waypoints = new LinkedList<>(waypoints);
        cinematics.put(this.name, this);
    }

    public void play(CinematicPlayer player, Player bukkitPlayer, boolean delay) {
        CinematicStartEvent event = new CinematicStartEvent(bukkitPlayer);
        event.callEvent();
        if (event.isCancelled()) return;

        player.setInCinematic(true);
        player.setPreviousGamemode(bukkitPlayer.getGameMode());
        player.setPreviousLoc(bukkitPlayer.getLocation());
        playInternal(player, bukkitPlayer, delay);
    }

    protected abstract void playInternal(CinematicPlayer player, Player bukkitPlayer, boolean delay);

    protected void addBars(CinematicPlayer player, Player bukkitPlayer, boolean delay) {
        if (delay) Cinematics.getInstance().getTitleHandler().playFade(bukkitPlayer, 10, 15, 20);
        bukkitPlayer.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN, 1));
        player.getTasks().add(Bukkit.getScheduler().runTaskLater(Cinematics.getInstance(), () -> {
            if (!bukkitPlayer.isOnline()) return;
            bukkitPlayer.setGameMode(GameMode.SPECTATOR);
        }, 10L));
    }

    protected void playNext(CinematicPlayer player, Player bukkitPlayer) {
        if (player.isInCinematic() && !player.getCinematicQueue().isEmpty()) {
            player.getTasks().add(Bukkit.getScheduler().runTaskLater(Cinematics.getInstance(), () -> {
                if (!bukkitPlayer.isOnline()) return;
                Cinematics.getInstance().getCinematicHandler().getCinematic(player.getCinematicQueue().pollFirst()).playInternal(player, bukkitPlayer, false);
            }, 5L));
        } else {
            player.getTasks().add(Bukkit.getScheduler().runTaskLater(Cinematics.getInstance(), player::exitCinematic, 5L));
        }
    }
}