package com.sanriofriends.dialogue;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.entity.SanrioEntity;
import com.sanriofriends.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Conversación con un personaje: él habla y tú eliges entre tres opciones (se hace clic en el chat).
 * Cada opción ejecuta "/sanrio dialogo <uuid> <accion>", que llega a {@link #handle}.
 */
public final class Dialogue {
    private Dialogue() {}

    // ------------------------------------------------------------------ utilidades

    private static String[] o(String label, String action) {
        return new String[]{label, action};
    }

    private static void voice(ServerPlayer p, SanrioEntity e) {
        p.level().playSound(null, e.blockPosition(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 0.7F, e.getCharacter().pitch);
    }

    private static void say(ServerPlayer p, SanrioEntity e, Component text) {
        SanrioCharacter c = e.getCharacter();
        p.sendSystemMessage(Component.literal("<" + c.displayName + "> ")
                .withStyle(Style.EMPTY.withColor(c.color).withBold(true))
                .append(text.copy().withStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false))));
        voice(p, e);
    }

    private static void say(ServerPlayer p, SanrioEntity e, String text) {
        say(p, e, Component.literal(text));
    }

    private static void narrate(ServerPlayer p, Component text) {
        p.sendSystemMessage(Component.literal("  ").append(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private static void narrate(ServerPlayer p, String text) {
        narrate(p, Component.literal(text));
    }

    /** Muestra hasta tres opciones clicables. */
    private static void options(ServerPlayer p, SanrioEntity e, String[]... opts) {
        int n = 1;
        for (String[] opt : opts) {
            MutableComponent line = Component.literal("  [" + n++ + "] " + opt[0]).withStyle(Style.EMPTY
                    .withColor(ChatFormatting.YELLOW)
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sanrio dialogo " + e.getUUID() + " " + opt[1]))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Haz clic para elegir"))));
            p.sendSystemMessage(line);
        }
    }

    private static int count(ServerPlayer p, Item item) {
        int n = 0;
        for (ItemStack s : p.getInventory().items) {
            if (s.is(item)) n += s.getCount();
        }
        for (ItemStack s : p.getInventory().offhand) {
            if (s.is(item)) n += s.getCount();
        }
        return n;
    }

    private static void consume(ServerPlayer p, Item item, int amount) {
        int left = amount;
        for (ItemStack s : p.getInventory().items) {
            if (left <= 0) return;
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
        for (ItemStack s : p.getInventory().offhand) {
            if (left <= 0) return;
            if (s.is(item)) {
                int take = Math.min(left, s.getCount());
                s.shrink(take);
                left -= take;
            }
        }
    }

    private static String pick(SanrioEntity e, String[] pool) {
        return pool[e.getRandom().nextInt(pool.length)];
    }

    // ------------------------------------------------------------------ inicio y menú

    public static void open(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        boolean friend = FriendData.isFriend(p, c);
        String text = pick(e, friend ? Lines.greetFriend(c) : Lines.greet(c));
        if (!p.level().isDay()) {
            text += friend
                    ? " Ya es de noche... Si quieres, puedes quedarte a dormir en mi casita."
                    : " Ya es de noche... ¿no quieres quedarte a dormir en mi casita? Aquí estarás a salvo.";
        } else if (p.getHealth() < p.getMaxHealth() * 0.5F) {
            text += " ¡Oh, estás herido! Si quieres, puedes descansar en mi casa.";
        }
        say(p, e, text);
        menu(p, e);
    }

    private static void menu(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        if (FriendData.isFriend(p, c)) {
            options(p, e, o("Charlar un rato", "chat"), o("Misiones", "quest"), o("Pedirle algo...", "companion"));
        } else {
            String stay = p.level().isDay() ? "¿Puedo quedarme en tu casa?" : "¡Sí, me quedo a dormir!";
            options(p, e, o("Charlar un rato", "chat"), o("¿Necesitas ayuda con algo?", "quest"), o(stay, "rest"));
        }
    }

    // ------------------------------------------------------------------ acciones

    public static void handle(ServerPlayer p, SanrioEntity e, String action) {
        SanrioCharacter c = e.getCharacter();
        boolean friend = FriendData.isFriend(p, c);
        if (!action.equals("follow") && !action.equals("stay")) {
            e.startTalking(p);
        }
        switch (action) {
            case "chat" -> chat(p, e);
            case "quest" -> quest(p, e);
            case "accept" -> accept(p, e);
            case "reward" -> reward(p, e);
            case "later" -> {
                say(p, e, Lines.farewell(c));
                menu(p, e);
            }
            case "deliver" -> deliver(p, e);
            case "abandon" -> abandon(p, e);
            case "rest" -> rest(p, e);
            case "companion" -> {
                if (!friend) {
                    say(p, e, "Primero hagamos algo juntos... ¡cuando me ayudes con una misión seremos amigos!");
                    menu(p, e);
                } else {
                    say(p, e, "¿Qué quieres que haga?");
                    options(p, e, o("¡Sígueme!", "follow"), o("Quédate aquí quieto", "stay"), o("Quedarme a descansar en tu casa", "rest"));
                }
            }
            case "follow" -> {
                if (!friend) {
                    say(p, e, "Todavía no somos amigos... ¿me ayudas con una misión?");
                    return;
                }
                e.setFollow(p.getUUID());
                e.stopTalking();
                say(p, e, "¡Vale, voy contigo!");
                narrate(p, c.displayName + " te seguirá a todas partes.");
                ((ServerLevel) p.level()).sendParticles(ParticleTypes.HEART, e.getX(), e.getY() + 1.4, e.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
            }
            case "stay" -> {
                if (!friend) {
                    say(p, e, "Todavía no somos amigos... ¿me ayudas con una misión?");
                    return;
                }
                e.setStaying(true);
                e.stopTalking();
                say(p, e, "¡Vale, me quedo aquí quietecito!");
                narrate(p, c.displayName + " se quedará quieto. Habla con él para que te siga de nuevo.");
            }
            default -> { }
        }
    }

    // ------------------------------------------------------------------ charlar y regalos

    private static void chat(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        say(p, e, pick(e, Lines.chat(c)));

        long today = p.level().getDayTime() / 24000L + 1L;
        if (FriendData.getLong(p, c, "gift") < today) {
            FriendData.setLong(p, c, "gift", today);
            giveGift(p, e);
        }
        menu(p, e);
    }

    private static void giveGift(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        int done = FriendData.completed(p, c);
        ItemStack food = new ItemStack(ModItems.FOOD.get(c).get(), done >= 3 ? 2 : 1);
        ItemStack extra = switch (c) {
            case KITTY -> new ItemStack(Items.APPLE, 2);
            case MELODY -> new ItemStack(Items.PINK_TULIP, 2);
            case KUROMI -> new ItemStack(Items.PURPLE_DYE, 2);
            case CINNAMOROLL -> new ItemStack(Items.SUGAR, 3);
            case PURIN -> new ItemStack(Items.EGG, 2);
        };
        Quests.give(p, food);
        Quests.give(p, extra);
        say(p, e, "¡Toma, un regalito para ti!");
        narrate(p, Component.literal("Has recibido " + food.getCount() + " × ").append(food.getHoverName())
                .append(Component.literal(" y " + extra.getCount() + " × ")).append(extra.getHoverName()));
    }

    // ------------------------------------------------------------------ misiones

    private static void quest(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        int done = FriendData.completed(p, c);
        Quests.Quest q = Quests.current(c, done);

        if (FriendData.questActive(p, c)) {
            say(p, e, Component.literal("¿Cómo va mi encargo? Necesito ").append(Quests.goal(q, done)).append(Component.literal(".")));
            int need = Quests.amount(q, done);
            int have = q.type() == Quests.Type.KILL ? FriendData.progress(p, c) : count(p, q.item().get());
            narrate(p, "Misión: " + q.title() + " (" + Math.min(have, need) + "/" + need + ")");
            options(p, e, o(q.type() == Quests.Type.KILL ? "¡Ya lo he hecho!" : "Entregar lo que tengo", "deliver"),
                    o("Abandonar la misión", "abandon"), o("Seguiré intentándolo", "later"));
        } else {
            say(p, e, Quests.text(q, done));
            narrate(p, "Misión: " + q.title());
            options(p, e, o("¡Acepto, cuenta conmigo!", "accept"), o("¿Qué recompensa hay?", "reward"), o("Ahora no puedo", "later"));
        }
    }

    private static void accept(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        if (FriendData.questActive(p, c)) {
            quest(p, e);
            return;
        }
        int done = FriendData.completed(p, c);
        Quests.Quest q = Quests.current(c, done);
        FriendData.setInt(p, c, "active", 1);
        FriendData.setInt(p, c, "progress", 0);
        say(p, e, "¡Gracias, gracias! Cuento contigo.");
        narrate(p, Component.literal("Misión aceptada: " + q.title() + ". Objetivo: ").append(Quests.goal(q, done)));
        menu(p, e);
    }

    private static void reward(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        int done = FriendData.completed(p, c);
        say(p, e, "Si me ayudas, te daré " + Quests.rewardText(c, done));
        options(p, e, o("¡Acepto, cuenta conmigo!", "accept"), o("Cuéntame de nuevo", "quest"), o("Ahora no puedo", "later"));
    }

    private static void abandon(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        FriendData.setInt(p, c, "active", 0);
        FriendData.setInt(p, c, "progress", 0);
        say(p, e, "No pasa nada, ya lo haré yo más tarde. ¡Gracias por intentarlo!");
        menu(p, e);
    }

    private static void deliver(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        if (!FriendData.questActive(p, c)) {
            quest(p, e);
            return;
        }
        int done = FriendData.completed(p, c);
        Quests.Quest q = Quests.current(c, done);
        int need = Quests.amount(q, done);

        if (q.type() == Quests.Type.KILL) {
            int prog = FriendData.progress(p, c);
            if (prog < need) {
                say(p, e, "Todavía quedan monstruos por ahí... ¡ánimo!");
                narrate(p, "Progreso: " + prog + "/" + need + " monstruos");
                options(p, e, o("Entregar lo que tengo", "deliver"), o("Abandonar la misión", "abandon"), o("Seguiré intentándolo", "later"));
                return;
            }
        } else {
            Item item = q.item().get();
            int have = count(p, item);
            if (have < need) {
                say(p, e, Component.literal("Todavía me faltan algunos... Necesito ").append(Quests.goal(q, done)).append(Component.literal(".")));
                narrate(p, "Llevas " + have + "/" + need);
                options(p, e, o("Entregar lo que tengo", "deliver"), o("Abandonar la misión", "abandon"), o("Seguiré intentándolo", "later"));
                return;
            }
            consume(p, item, need);
        }
        complete(p, e, q, done);
    }

    private static void complete(ServerPlayer p, SanrioEntity e, Quests.Quest q, int done) {
        SanrioCharacter c = e.getCharacter();
        Quests.giveReward(p, c, done);
        FriendData.setInt(p, c, "done", done + 1);
        FriendData.setInt(p, c, "active", 0);
        FriendData.setInt(p, c, "progress", 0);

        ServerLevel level = (ServerLevel) p.level();
        level.sendParticles(ParticleTypes.HEART, e.getX(), e.getY() + 1.4, e.getZ(), 10, 0.4, 0.4, 0.4, 0.0);
        level.playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);

        say(p, e, Lines.thanks(c));
        narrate(p, "¡Misión completada: " + q.title() + "! Recibes " + Quests.rewardText(c, done));
        if (done == 0) {
            say(p, e, "¡Ahora somos amigos! Si quieres, puedo seguirte o quedarme quieto donde me digas.");
            narrate(p, "¡Tú y " + c.displayName + " ahora sois amigos! Habla con él y elige «Pedirle algo...».");
        } else {
            narrate(p, "Vuestra amistad: " + FriendData.friendTitle(done + 1));
        }
        menu(p, e);
    }

    // ------------------------------------------------------------------ quedarse en su casa

    private static void rest(ServerPlayer p, SanrioEntity e) {
        SanrioCharacter c = e.getCharacter();
        ServerLevel level = (ServerLevel) p.level();
        BlockPos home = e.getHome() != null ? e.getHome() : e.blockPosition();

        say(p, e, "¡Claro! Mi casa es tu casa. Descansa todo lo que quieras.");
        p.setRespawnPosition(level.dimension(), home, p.getYRot(), true, true);
        p.heal(p.getMaxHealth());
        p.getFoodData().eat(8, 0.8F);
        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        level.playSound(null, p.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
        level.sendParticles(ParticleTypes.HEART, p.getX(), p.getY() + 1.8, p.getZ(), 6, 0.4, 0.3, 0.4, 0.0);

        boolean skipped = false;
        if (!level.isDay() && level.getServer().getPlayerCount() == 1) {
            long t = level.getDayTime();
            level.setDayTime(t + (24000L - t % 24000L));
            level.setWeatherParameters(6000, 0, false, false);
            skipped = true;
        }
        narrate(p, "Has descansado en la casa de " + c.displayName + ": vida y comida recuperadas, y ahora reapareces aquí."
                + (skipped ? " Ha amanecido." : ""));
        menu(p, e);
    }
}
