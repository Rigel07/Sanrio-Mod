package com.sanriofriends.dialogue;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.registry.ModBlocks;
import com.sanriofriends.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Misiones de cada personaje (3 distintas que se repiten con más cantidad) y sus recompensas. */
public final class Quests {
    public enum Type { COLLECT, KILL }

    /** @param text usa %d para la cantidad. */
    public record Quest(Type type, Supplier<Item> item, int amount, String title, String text) {}

    private static final Map<SanrioCharacter, List<Quest>> QUESTS = new EnumMap<>(SanrioCharacter.class);

    static {
        QUESTS.put(SanrioCharacter.KITTY, List.of(
                new Quest(Type.COLLECT, () -> Items.APPLE, 5, "Tarta de manzana",
                        "Quiero hornear una tarta de manzana, pero me faltan manzanas. ¿Me traerías %d?"),
                new Quest(Type.COLLECT, () -> Items.EGG, 6, "Bizcocho esponjoso",
                        "Para el bizcocho necesito huevos. ¿Podrías traerme %d?"),
                new Quest(Type.COLLECT, () -> Items.RED_DYE, 4, "Lazos nuevos",
                        "Quiero hacer lazos rojos nuevos para mis amigas. ¿Me traes %d tintes rojos?")));
        QUESTS.put(SanrioCharacter.MELODY, List.of(
                new Quest(Type.COLLECT, () -> Items.PINK_TULIP, 4, "Flores para mi capucha",
                        "Quiero decorar mi capucha con flores. ¿Me traes %d tulipanes rosas?"),
                new Quest(Type.COLLECT, () -> Items.SWEET_BERRIES, 8, "Merienda en el bosque",
                        "Voy a preparar un pastel de bayas. ¿Me ayudas con %d bayas dulces?"),
                new Quest(Type.COLLECT, () -> Items.COOKIE, 6, "Galletas para Flat",
                        "A mi amigo Flat le encantan las galletas. ¿Me traes %d?")));
        QUESTS.put(SanrioCharacter.KUROMI, List.of(
                new Quest(Type.KILL, () -> Items.AIR, 5, "¡Fuera, monstruos!",
                        "Unos monstruos me molestan muchísimo. ¡Derrota a %d y te lo agradeceré... un poquito!"),
                new Quest(Type.COLLECT, () -> Items.SPIDER_EYE, 3, "Poción traviesa",
                        "Estoy preparando una poción muy traviesa. Necesito %d ojos de araña."),
                new Quest(Type.COLLECT, () -> Items.BONE, 8, "Huesos para Baku",
                        "Mi amigo Baku tiene hambre. ¿Me traes %d huesos?")));
        QUESTS.put(SanrioCharacter.CINNAMOROLL, List.of(
                new Quest(Type.COLLECT, () -> Items.SUGAR, 6, "Glaseado para los rollitos",
                        "Me falta azúcar para el glaseado. ¿Me traes %d?"),
                new Quest(Type.COLLECT, () -> Items.BREAD, 4, "Pan para el café",
                        "Los clientes del café quieren más pan. ¿Me traerías %d?"),
                new Quest(Type.COLLECT, () -> Items.WHITE_WOOL, 6, "Cojines de nube",
                        "Quiero hacer cojines blandos como nubes. ¿Me traes %d lanas blancas?")));
        QUESTS.put(SanrioCharacter.PURIN, List.of(
                new Quest(Type.COLLECT, () -> Items.EGG, 6, "Huevos para el pudín",
                        "¡Quiero hacer pudín! Necesito %d huevos. ¿Me ayudas?"),
                new Quest(Type.COLLECT, () -> Items.HONEY_BOTTLE, 2, "Miel dulce",
                        "Al pudín le sienta muy bien la miel. ¿Me traes %d botellas?"),
                new Quest(Type.COLLECT, () -> Items.COCOA_BEANS, 6, "Cacao para el caramelo",
                        "Para el caramelo necesito cacao. ¿Me traes %d semillas?")));
    }

    private Quests() {}

    public static Quest current(SanrioCharacter c, int completed) {
        List<Quest> list = QUESTS.get(c);
        return list.get(completed % list.size());
    }

    /** La cantidad aumenta cada vez que se repite el ciclo de 3 misiones. */
    public static int amount(Quest q, int completed) {
        return q.amount() * (1 + completed / 3);
    }

    /** Texto de la misión con la cantidad ya puesta. */
    public static String text(Quest q, int completed) {
        return String.format(q.text(), amount(q, completed));
    }

    /** "5 × Manzana" (o "5 monstruos"). */
    public static MutableComponent goal(Quest q, int completed) {
        int amount = amount(q, completed);
        if (q.type() == Type.KILL) {
            return Component.literal(amount + " monstruos");
        }
        return Component.literal(amount + " × ").append(Component.translatable(q.item().get().getDescriptionId()));
    }

    // ------------------------------------------------------------------ recompensas

    /** Descripción corta de lo que da la misión nº 'completed' (0, 1, 2...). */
    public static String rewardText(SanrioCharacter c, int completed) {
        return switch (Math.min(completed, 3)) {
            case 0 -> "3 dulces de mi receta, 2 esmeraldas y mi amistad.";
            case 1 -> "un peluche mío, 2 dulces y 3 esmeraldas.";
            case 2 -> "mi amuleto especial, 3 dulces y 4 esmeraldas.";
            default -> "4 dulces, 2 lámparas estrella y 5 esmeraldas.";
        };
    }

    public static void giveReward(ServerPlayer p, SanrioCharacter c, int completed) {
        Item food = ModItems.FOOD.get(c).get();
        switch (Math.min(completed, 3)) {
            case 0 -> {
                give(p, new ItemStack(food, 3));
                give(p, new ItemStack(Items.EMERALD, 2));
                p.giveExperiencePoints(30);
            }
            case 1 -> {
                give(p, new ItemStack(ModBlocks.PLUSH.get(c).get()));
                give(p, new ItemStack(food, 2));
                give(p, new ItemStack(Items.EMERALD, 3));
                p.giveExperiencePoints(50);
            }
            case 2 -> {
                give(p, new ItemStack(ModItems.CHARM.get(c).get()));
                give(p, new ItemStack(food, 3));
                give(p, new ItemStack(Items.EMERALD, 4));
                p.giveExperiencePoints(80);
            }
            default -> {
                give(p, new ItemStack(food, 4));
                give(p, new ItemStack(ModBlocks.STAR_LAMP.get(), 2));
                give(p, new ItemStack(Items.EMERALD, 5));
                p.giveExperiencePoints(100);
            }
        }
    }

    public static void give(ServerPlayer p, ItemStack stack) {
        ItemStack s = stack.copy();
        p.getInventory().add(s);
        if (!s.isEmpty()) {
            p.drop(s, false);
        }
    }
}
