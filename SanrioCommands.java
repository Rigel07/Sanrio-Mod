package com.sanriofriends.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.dialogue.Dialogue;
import com.sanriofriends.dialogue.FriendData;
import com.sanriofriends.dialogue.Quests;
import com.sanriofriends.entity.SanrioEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

/**
 * /sanrio amigos            -> tu libreta de amigos y misiones
 * /sanrio dialogo <id> <a>  -> lo usan los clics del chat (no hace falta escribirlo)
 */
@Mod.EventBusSubscriber(modid = SanrioFriends.MOD_ID)
public final class SanrioCommands {
    private SanrioCommands() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("sanrio")
                .then(Commands.literal("dialogo")
                        .then(Commands.argument("id", UuidArgument.uuid())
                                .then(Commands.argument("accion", StringArgumentType.word())
                                        .executes(ctx -> {
                                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                                            UUID id = UuidArgument.getUuid(ctx, "id");
                                            String action = StringArgumentType.getString(ctx, "accion");
                                            Entity ent = p.serverLevel().getEntity(id);
                                            if (ent instanceof SanrioEntity se && se.isAlive() && se.distanceToSqr(p) < 100.0D) {
                                                Dialogue.handle(p, se, action);
                                                return 1;
                                            }
                                            p.sendSystemMessage(Component.literal("Estás demasiado lejos para hablar. ¡Acércate!")
                                                    .withStyle(ChatFormatting.GRAY));
                                            return 0;
                                        }))))
                .then(Commands.literal("amigos").executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    p.sendSystemMessage(Component.literal("=== Tus amigos de Sanrio ===").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
                    for (SanrioCharacter c : SanrioCharacter.values()) {
                        int done = FriendData.completed(p, c);
                        StringBuilder sb = new StringBuilder(c.displayName + " — " + FriendData.friendTitle(done)
                                + " (misiones completadas: " + done + ")");
                        if (FriendData.questActive(p, c)) {
                            sb.append("  ·  Misión: ").append(Quests.current(c, done).title());
                        }
                        p.sendSystemMessage(Component.literal(sb.toString()).withStyle(Style.EMPTY.withColor(c.color)));
                    }
                    return 1;
                })));
    }
}
