package me.jacob.cinematics.handler;

import lombok.Getter;
import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.objects.cinematics.Cinematic;
import me.jacob.cinematics.objects.cinematics.LinearCinematic;
import me.jacob.cinematics.objects.cinematics.SplineCinematic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Level;

public class CinematicHandler {

    private final Cinematics plugin;

    @Getter private boolean disableChat;

    public CinematicHandler() {
        this.plugin = Cinematics.getInstance();
        loadCinematics();
    }

    public void loadCinematics() {

        Cinematic.getCinematics().clear();

        plugin.reloadConfig();
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();

        this.disableChat = plugin.getConfig().getBoolean("disable-chat");

        ConfigurationSection cinematics = plugin.getConfig().getConfigurationSection("cinematics");

        if (cinematics == null) {
            plugin.getLogger().log(Level.INFO, "No cinematics to load");
            return;
        }

        for (String cinematic : cinematics.getKeys(false)) {
            ConfigurationSection section = cinematics.getConfigurationSection(cinematic);

            LinkedList<Location> waypoints = new LinkedList<>();
            for (String waypoint : Objects.requireNonNull(section).getStringList("waypoints")) {
                String[] parts = waypoint.split(",");
                waypoints.add(new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5])));
            }

            int speed = section.getInt("speed");
            int worldTime = section.getInt("world-time");

            String type = Objects.requireNonNull(section.getString("type"));
            if (type.equalsIgnoreCase("linear")) {
                double smoothness = section.getDouble("smoothness");
                new LinearCinematic(cinematic, speed, worldTime, smoothness, waypoints);
                plugin.getLogger().log(Level.INFO, "Loaded " + cinematic + " cinematic with " + waypoints.size() + " waypoints, smoothness " + smoothness + ", speed " + speed + ", and world time " + worldTime);
            } else if (type.equalsIgnoreCase("spline")) {
                new SplineCinematic(cinematic, speed, worldTime, waypoints);
                plugin.getLogger().log(Level.INFO, "Loaded " + cinematic + " spline cinematic with " + waypoints.size() + " waypoints, speed " + speed + ", and world time " + worldTime);
            }
        }
    }

    public void saveCinematic(Cinematic cinematic) {
        if (plugin.getConfig().getConfigurationSection("cinematics") == null) {
            plugin.getConfig().createSection("cinematics");
        }
        ConfigurationSection section = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("cinematics")).createSection(cinematic.getName());

        section.set("speed", cinematic.getSpeed());
        section.set("world-time", cinematic.getWorldTime());
        if (cinematic instanceof SplineCinematic) {
            section.set("type", "spline");
        } else if (cinematic instanceof LinearCinematic linearCinematic) {
            section.set("type", "linear");
            section.set("smoothness", linearCinematic.getSmoothness());
        }
        section.set("waypoints", cinematic.getWaypoints().stream().map(loc -> loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch()).toList());

        plugin.saveConfig();
    }

    public Cinematic getCinematic(String name) {
        return Cinematic.getCinematics().get(name);
    }

}
