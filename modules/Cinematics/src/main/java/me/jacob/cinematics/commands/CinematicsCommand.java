package me.jacob.cinematics.commands;

import me.jacob.cinematics.Cinematics;
import me.jacob.cinematics.commands.tabcompleters.CinematicsTabCompleter;
import me.jacob.cinematics.objects.CinematicPlayer;
import me.jacob.cinematics.objects.cinematics.Cinematic;
import me.jacob.cinematics.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CinematicsCommand implements CommandExecutor {

    private final Cinematics plugin;

    public CinematicsCommand() {
        this.plugin = Cinematics.getInstance();
        Objects.requireNonNull(Cinematics.getInstance().getCommand("cinematics")).setTabCompleter(new CinematicsTabCompleter());
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.color("&cOnly players may use this command"));
            return true;
        }

        CinematicPlayer player = CinematicPlayer.getByUUID(((Player) sender).getUniqueId());
        if (player == null) return true;

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("create")) {

                String name = args[1];
                if (Cinematic.getCinematics().containsKey(name)) {
                    sender.sendMessage(ColorUtil.color("&cThis cinematic already exists"));
                    return true;
                }

                player.setCreatingCinematic(true);
                player.setCreatingCinematicName(name);
                player.setWaypoints(new ArrayList<>());

                sender.sendMessage(ColorUtil.color("&aType 'add' to add a waypoint, 'end [spline] [speed] [world time] [smoothness]' to end & save, and 'cancel' to cancel."));

            } else if (args[0].equalsIgnoreCase("play")) {

                String name = args[1];
                Cinematic cinematic = Cinematic.getCinematics().getOrDefault(name, null);
                if (cinematic != null) {
                    sender.sendMessage(ColorUtil.color("&aStarting cinematic " + cinematic.getName()));
                    player.playCinematics(List.of(args[1]));
                } else {
                    sender.sendMessage(ColorUtil.color("&cThis cinematic doesn't exist"));
                }

            } else if (args[0].equalsIgnoreCase("playsequence")) {

                List<String> names = Arrays.stream(args).toList().subList(1, args.length);
                player.playCinematics(names);
                sender.sendMessage(ColorUtil.color("&aPlaying cinematics in order: " + String.join(",", names)));

            } else {
                sender.sendMessage(ColorUtil.color("&cInvalid format"));
            }
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("end")) {
                if (!player.isInCinematic()) {
                    sender.sendMessage(ColorUtil.color("&cYou are not in a cinematic"));
                    return true;
                }
                player.exitCinematic();
                sender.sendMessage(ColorUtil.color("&aEnded cinematic"));
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ColorUtil.color("&f&lCinematics:&a " + String.join(",", Cinematic.getCinematics().keySet())));

            } else if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ColorUtil.color("&aReloading cinematics from config..."));
                long time = System.currentTimeMillis();
                try {
                    plugin.getCinematicHandler().loadCinematics();
                } catch (Exception ex) {
                    sender.sendMessage(ColorUtil.color("&cError reloading cinematics from config. Check console."));
                    return true;
                }
                sender.sendMessage(ColorUtil.color("&aReloaded cinematics from config in " + (System.currentTimeMillis() - time) + "ms"));
            } else {
                sender.sendMessage(ColorUtil.color("&cInvalid format"));
            }
            return true;
        } else {
            sender.sendMessage(ColorUtil.color("&cInvalid format"));
            return true;
        }
    }
}