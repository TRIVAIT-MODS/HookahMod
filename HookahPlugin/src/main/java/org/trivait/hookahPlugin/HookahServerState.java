package org.trivait.hookahPlugin;

import org.bukkit.configuration.file.FileConfiguration;

public class HookahServerState {

    public static boolean networkingEnabled = true;
    public static boolean effectsEnabled = true;

    private static HookahPlugin plugin;

    public static void init(HookahPlugin instance) {
        plugin = instance;
        load();
    }

    public static void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();
        networkingEnabled = cfg.getBoolean("networkingEnabled", true);
        effectsEnabled = cfg.getBoolean("effectsEnabled", true);
    }

    public static void save() {
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("networkingEnabled", networkingEnabled);
        cfg.set("effectsEnabled", effectsEnabled);
        plugin.saveConfig();
    }
}
