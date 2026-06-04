package org.trivait.hookahPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class HookahCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("brewingStand.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§eUsage: /brewingStand <toggle|toggleEffects>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "toggle" -> {
                HookahServerState.networkingEnabled = !HookahServerState.networkingEnabled;
                HookahServerState.save();
                sender.sendMessage("§6[BrewingStandSmoke] §fNetworking " + (HookahServerState.networkingEnabled ? "§aenabled" : "§cdisabled"));
            }
            case "toggleeffects" -> {
                HookahServerState.effectsEnabled = !HookahServerState.effectsEnabled;
                HookahServerState.save();
                sender.sendMessage("§6[BrewingStandSmoke] §fEffects " + (HookahServerState.effectsEnabled ? "§aenabled" : "§cdisabled"));
            }
            default -> sender.sendMessage("§eUsage: /brewingStand <toggle|toggleEffects>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1)
            return List.of("toggle", "toggleEffects").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        return List.of();
    }
}
