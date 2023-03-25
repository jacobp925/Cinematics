package me.jacob.cinematics.commands.tabcompleters;

import me.jacob.cinematics.objects.cinematics.Cinematic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CinematicsTabCompleter implements TabCompleter {

    private final String[] args;

    public CinematicsTabCompleter() {
        this.args = new String[]{"create", "list", "play", "playsequence", "reload", "end"};
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) return null;

        List<String> subCommands = new ArrayList<>();

        if (args.length == 1) {

            if (args[0].isEmpty()) {
                subCommands.addAll(List.of(this.args));
                return subCommands;
            }

            for (String arg : this.args) {
                if (arg.startsWith(args[0])) {
                    subCommands.add(arg);
                }
            }
            return subCommands;

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("play") || args[0].equalsIgnoreCase("playsequence")) {

                if (args[1].isEmpty()) {
                    subCommands.addAll(Cinematic.getCinematics().keySet());
                    return subCommands;
                }

                for (String cinematicName : Cinematic.getCinematics().keySet()) {
                    if (cinematicName.startsWith(args[1])) {
                        subCommands.add(cinematicName);
                    }
                }
                return subCommands;

            }
        } else if (args.length > 2) {

            if (args[0].equalsIgnoreCase("playsequence")) {
                for (String cinematicName : Cinematic.getCinematics().keySet()) {
                    if (cinematicName.startsWith(args[args.length - 1]) || args[args.length - 1].isEmpty()) {
                        subCommands.add(cinematicName);
                    }
                }
                return subCommands;
            }

        }
        return null;
    }
}
