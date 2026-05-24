package org.trivait.hookahmod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trivait.hookahmod.network.ModNetworking;
import org.trivait.hookahmod.server.HookahServerState;

public class HookahMod implements ModInitializer {
    public static final String MOD_ID = "hookahmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        HookahServerState.load();
        ModNetworking.register();
    }
}
