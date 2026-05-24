package org.trivait.hookahPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class HookahCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hookahplugin.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§eUsage: /hookah <toggle|toggleEffects>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "toggle" -> {
                HookahServerState.networkingEnabled = !HookahServerState.networkingEnabled;
                HookahServerState.save();
                sender.sendMessage("§6[HookahPlugin] §fNetworking " + (HookahServerState.networkingEnabled ? "§aenabled" : "§cdisabled"));
            }
            case "toggleeffects" -> {
                HookahServerState.effectsEnabled = !HookahServerState.effectsEnabled;
                HookahServerState.save();
                sender.sendMessage("§6[HookahPlugin] §fEffects " + (HookahServerState.effectsEnabled ? "§aenabled" : "§cdisabled"));
            }
            default -> sender.sendMessage("§eUsage: /hookah <toggle|toggleEffects>");
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
