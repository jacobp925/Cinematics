package me.jacob.cinematics.objects.cinematics;

import lombok.Getter;
import lombok.Setter;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.LinkedList;

@Getter
@Setter
public class LinearCinematic extends Cinematic {

    private double smoothness;

    public LinearCinematic(String name, int speed, int worldTime, double smoothness, LinkedList<Location> waypoints) {
        this(name, speed, worldTime, smoothness, waypoints, false);
    }

    public LinearCinematic(String name, int speed, int worldTime, double smoothness, LinkedList<Location> waypoints, boolean creating) {
        super(name, speed, worldTime, waypoints);
        this.smoothness = smoothness;
        if (creating) Cinematics.getInstance().getCinematicHandler().saveCinematic(this);
    }

    protected void playInternal(CinematicPlayer player, Player bukkitPlayer, boolean delay) {
        moveToWaypoint(player, bukkitPlayer, 0, delay, false);
    }

    private void moveToWaypoint(CinematicPlayer player, Player bukkitPlayer, int pos, boolean delay, boolean fading) {

        bukkitPlayer.setPlayerTime(getWorldTime(), false);

        if (pos == getWaypoints().size() || !player.isInCinematic()) {
            playNext(player, bukkitPlayer);
            return;
        }

        Location end = getWaypoints().get(pos);

        if (pos == 0) {
            addBars(player, bukkitPlayer, delay);
        }

        new BukkitRunnable() {
            int fadeCount = 0;
            Location currentLoc = pos == 0 ? end.clone() : bukkitPlayer.getLocation();

            @Override
            public void run() {

                if (!bukkitPlayer.isOnline()) {
                    this.cancel();
                    return;
                }

                if (pos == 0) {
                    bukkitPlayer.teleport(end);
                }

                Vector movement = end.toVector().clone().subtract(currentLoc.toVector()).normalize().multiply(smoothness);
                Location teleport = currentLoc.clone();

                teleport.add(movement);

                Vector headMovement = getWaypoints().get(pos).getDirection().subtract(currentLoc.getDirection()).multiply(smoothness / 10);
                teleport.setDirection(currentLoc.getDirection().add(headMovement));

                if (!fading && fadeCount == 0 && pos == getWaypoints().size() - 1 && currentLoc.distance(end) <= 2) {
                    fadeCount++;
                    Cinematics.getInstance().getTitleHandler().playFade(bukkitPlayer, 10, 15, 10);
                }

                if (currentLoc.distance(end) <= 1 || !player.isInCinematic()) {
                    this.cancel();
                    moveToWaypoint(player, bukkitPlayer, pos + 1, delay, fadeCount > 0);
                    return;
                }

                bukkitPlayer.teleport(teleport);
                currentLoc = teleport;
            }
        }.runTaskTimer(Cinematics.getInstance(), delay ? (pos == 0 ? 20 : getSpeed()) : 0, getSpeed());
    }

}