package org.trivait.hookahmod.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.trivait.hookahmod.HookahMod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HookahServerState {

    public static boolean networkingEnabled = true;
    public static boolean effectsEnabled = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path statePath;

    public static void load() {
        statePath = FabricLoader.getInstance().getConfigDir().resolve("brewing_stand_server_state.json");
        if (!Files.exists(statePath)) { save(); return; }
        try {
            StateData data = GSON.fromJson(Files.readString(statePath), StateData.class);
            if (data != null) {
                networkingEnabled = data.networkingEnabled;
                effectsEnabled = data.effectsEnabled;
            }
        } catch (IOException e) {
            HookahMod.LOGGER.error("Failed to load BrewingStandSmoke server state", e);
        }
    }

    public static void save() {
        if (statePath == null) return;
        try {
            Files.writeString(statePath, GSON.toJson(new StateData(networkingEnabled, effectsEnabled)));
        } catch (IOException e) {
            HookahMod.LOGGER.error("Failed to save hookah server state", e);
        }
    }

    private record StateData(boolean networkingEnabled, boolean effectsEnabled) {}
}
