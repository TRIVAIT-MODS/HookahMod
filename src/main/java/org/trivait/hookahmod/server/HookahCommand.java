package org.trivait.hookahmod.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class HookahCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("hookah")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("toggle")
                    .executes(ctx -> {
                        HookahServerState.networkingEnabled = !HookahServerState.networkingEnabled;
                        HookahServerState.save();
                        String state = HookahServerState.networkingEnabled ? "enabled" : "disabled";
                        ctx.getSource().sendSuccess(() -> Component.literal("[HookahMod] Networking " + state), true);
                        return 1;
                    })
                )
                .then(Commands.literal("toggleEffects")
                    .executes(ctx -> {
                        HookahServerState.effectsEnabled = !HookahServerState.effectsEnabled;
                        HookahServerState.save();
                        String state = HookahServerState.effectsEnabled ? "enabled" : "disabled";
                        ctx.getSource().sendSuccess(() -> Component.literal("[HookahMod] Effects " + state), true);
                        return 1;
                    })
                )
        );
    }
}
