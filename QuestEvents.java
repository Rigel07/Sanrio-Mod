package com.sanriofriends.event;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.dialogue.FriendData;
import com.sanriofriends.dialogue.Quests;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SanrioFriends.MOD_ID)
public final class QuestEvents {
    private QuestEvents() {}

    /** Cuenta los monstruos derrotados para las misiones de tipo "derrota monstruos". */
    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player) || !(event.getEntity() instanceof Enemy)) {
            return;
        }
        for (SanrioCharacter c : SanrioCharacter.values()) {
            if (!FriendData.questActive(player, c)) continue;
            int done = FriendData.completed(player, c);
            Quests.Quest q = Quests.current(c, done);
            if (q.type() != Quests.Type.KILL) continue;

            int need = Quests.amount(q, done);
            int prog = FriendData.progress(player, c);
            if (prog >= need) continue;

            prog++;
            FriendData.setInt(player, c, "progress", prog);
            player.displayClientMessage(Component.literal(c.displayName + ": " + prog + "/" + need + " monstruos"), true);
            if (prog >= need) {
                player.sendSystemMessage(Component.literal("<" + c.displayName + "> ")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
                        .append(Component.literal("(¡Has derrotado a suficientes monstruos! Vuelve a hablar conmigo.)")
                                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)));
            }
        }
    }

    /** La amistad y las misiones sobreviven a la muerte. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundTag old = event.getOriginal().getPersistentData();
        if (old.contains(FriendData.KEY)) {
            event.getEntity().getPersistentData().put(FriendData.KEY, old.getCompound(FriendData.KEY).copy());
        }
    }
}
