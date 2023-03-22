package me.jacob.cinematics;

import lombok.Getter;
import me.jacob.cinematics.commands.CinematicsCommand;
import me.jacob.cinematics.handler.ChatHandler;
import me.jacob.cinematics.handler.CinematicHandler;
import me.jacob.cinematics.handler.TitleHandler;
import me.jacob.cinematics.listeners.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class Cinematics extends JavaPlugin {

    private static Cinematics plugin;

    private CinematicHandler cinematicHandler;
    private ChatHandler chatHandler;
    private TitleHandler titleHandler;

    public void onEnable() {
        plugin = this;

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

    public static Cinematics getInstance() {
        return plugin;
    }

}
