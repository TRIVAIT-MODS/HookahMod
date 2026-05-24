package org.trivait.hookahmod.server;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class HookahCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("hookah")
                .requires(src -> {
                    ServerPlayer player = src.getPlayer();
                    if (player != null) {
                        return src.getServer().getPlayerList().isOp(player.nameAndId());
                    }
                    return src.getEntity() == null;
                })
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
