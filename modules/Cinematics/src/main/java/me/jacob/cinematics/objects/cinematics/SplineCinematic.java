package me.jacob.cinematics.objects.cinematics;

import lombok.Getter;
import lombok.Setter;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.CinematicPlayer;
import me.jacob.cinematics.objects.cinematics.interpolation.CatmullRomSpline;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class SplineCinematic extends Cinematic {

    public SplineCinematic(String name, int speed, int worldTime, ArrayList<Location> waypoints) {
        this(name, speed, worldTime, waypoints, false);
    }

    public SplineCinematic(String name, int speed, int worldTime, ArrayList<Location> waypoints, boolean creating) {
        super(name, speed, worldTime, waypoints);
        if (creating) Cinematics.getInstance().getCinematicHandler().saveCinematic(this);
    }

    protected void playInternal(CinematicPlayer player, Player bukkitPlayer, boolean delay) {

        bukkitPlayer.setPlayerTime(getWorldTime(), false);

        Location phantomLoc = getWaypoints().get(getWaypoints().size() - 1);
        List<Vector> controlPoints = getWaypoints().stream().map(Location::toVector).collect(Collectors.toList());
        controlPoints.add(phantomLoc.toVector());
        List<Vector> controlPointsDirections = getWaypoints().stream().map(Location::getDirection).collect(Collectors.toList());
        controlPointsDirections.add(phantomLoc.getDirection());

        final double totalLength = IntStream.range(0, controlPoints.size() - 1).mapToDouble(i -> controlPoints.get(i).distance(controlPoints.get(i + 1))).sum();
        final double speed = 0.001 * getSpeed();
        Vector end = controlPoints.get(controlPoints.size() - 1);

        addBars(player, bukkitPlayer, delay);

        new BukkitRunnable() {

            int fadeCount = 0;
            double positionOnCurve = 0;
            int elapsedTime = 0;
            Vector playerPosition = controlPoints.get(0);
            Vector playerDirection = controlPointsDirections.get(0);

            @Override
            public void run() {
                if (positionOnCurve < 1.0) {

                    Vector splinePosition = CatmullRomSpline.interpolate(positionOnCurve, controlPoints);
                    Vector splineDirection = CatmullRomSpline.interpolate(positionOnCurve, controlPointsDirections);

                    if (splinePosition == null || splineDirection == null) {
                        end(this, player, bukkitPlayer);
                        return;
                    }

                    Vector positionDirection = splinePosition.subtract(playerPosition);
                    Vector directionDirection = splineDirection.subtract(playerDirection);

                    double distance = speed * elapsedTime++;

                    playerPosition = playerPosition.add(positionDirection.multiply(distance));
                    playerDirection = playerDirection.add(directionDirection.multiply(distance));

                    positionOnCurve += distance / totalLength;

                    if (fadeCount == 0 && playerPosition.distance(end) <= 4) {
                        fadeCount++;
                        Cinematics.getInstance().getTitleHandler().playFade(bukkitPlayer, 10, 15, 10);
                    }

                    if (playerPosition.distance(end) <= 1 || !player.isInCinematic()) {
                        end(this, player, bukkitPlayer);
                        return;
                    }

                    bukkitPlayer.teleport(playerPosition.toLocation(bukkitPlayer.getWorld()).setDirection(playerDirection));

                }
            }
        }.runTaskTimer(Cinematics.getInstance(), delay ? 20 : getSpeed(), getSpeed());

    }

    public void end(BukkitRunnable bukkitRunnable, CinematicPlayer player, Player bukkitPlayer) {
        bukkitRunnable.cancel();
        playNext(player, bukkitPlayer);
    }
}