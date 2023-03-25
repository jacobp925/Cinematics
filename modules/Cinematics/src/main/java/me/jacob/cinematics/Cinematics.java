package me.jacob.cinematics;

import lombok.Getter;
import me.jacob.cinematics.commands.CinematicsCommand;
import me.jacob.cinematics.handler.ChatHandler;
import me.jacob.cinematics.handler.CinematicHandler;
import me.jacob.cinematics.handler.TitleHandler;
import me.jacob.cinematics.listeners.PlayerListener;
import me.jacob.cinematics.nms.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Getter
public class Cinematics extends JavaPlugin {

    private static Cinematics plugin;

    private NMSVersion nmsVersion;

    private CinematicHandler cinematicHandler;
    private ChatHandler chatHandler;
    private TitleHandler titleHandler;

    public void onEnable() {
        plugin = this;

        setupNMS();

        cinematicHandler = new CinematicHandler();
        titleHandler = new TitleHandler();
        chatHandler = new ChatHandler();

        registerEvents();
        registerCommands();

        getServer().getConsoleSender().sendMessage("Cinematics has been enabled");
    }

    @Override
    public void onDisable() {
        plugin = null;
        getServer().getConsoleSender().sendMessage("Cinematics has been disabled");
    }

    public void registerEvents() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        Objects.requireNonNull(getCommand("cinematics")).setExecutor(new CinematicsCommand());
    }

    private void setupNMS() {
        String internalsName = "";
        try {
            internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            nmsVersion = (NMSVersion) Class.forName("me.jacob.cinematics.nms." + internalsName + ".NMSWrapper").getDeclaredConstructor().newInstance(null);
            getServer().getConsoleSender().sendMessage("Using NMS version " + internalsName);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Your server version " + internalsName + " isn't supported!");
        }
    }

    public static Cinematics getInstance() {
        return plugin;
    }

}
