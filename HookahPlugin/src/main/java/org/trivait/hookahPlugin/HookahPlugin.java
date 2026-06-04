package org.trivait.hookahPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class HookahPlugin extends JavaPlugin {

    private static final String CHANNEL_C2S = "hookahmod:hookah_start";
    private static final String CHANNEL_S2C = "hookahmod:hookah_smoke";

    @Override
    public void onEnable() {
        // Load persisted state (networking/effects toggles)
        HookahServerState.init(this);

        // Register plugin message channels
        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_C2S, new HookahMessageListener(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL_S2C);

        // Register /hookah command
        HookahCommand cmd = new HookahCommand();
        getCommand("brewingStand").setExecutor(cmd);
        getCommand("brewingStand").setTabCompleter(cmd);

        getLogger().info("BrewingStandSmoke enabled. Networking=" + HookahServerState.networkingEnabled
                + " Effects=" + HookahServerState.effectsEnabled);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterIncomingPluginChannel(this, CHANNEL_C2S);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this, CHANNEL_S2C);
        getLogger().info("BrewingStandSmoke disabled.");
    }
}
