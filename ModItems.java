package com.sanriofriends.registry;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.item.CharmItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SanrioFriends.MOD_ID);

    public static final Map<SanrioCharacter, RegistryObject<Item>> FOOD = new EnumMap<>(SanrioCharacter.class);
    public static final Map<SanrioCharacter, RegistryObject<Item>> CHARM = new EnumMap<>(SanrioCharacter.class);
    public static final Map<SanrioCharacter, RegistryObject<Item>> EGG = new EnumMap<>(SanrioCharacter.class);

    static {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            FOOD.put(c, ITEMS.register(c.foodId, () -> new Item(new Item.Properties().food(foodFor(c)))));
            CHARM.put(c, ITEMS.register(c.charmId, () -> charmFor(c)));
            EGG.put(c, ITEMS.register(c.id + "_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.type(c), c.eggBg, c.eggFg, new Item.Properties())));
        }
    }

    private ModItems() {}

    private static FoodProperties foodFor(SanrioCharacter c) {
        FoodProperties.Builder b = new FoodProperties.Builder().nutrition(8).saturationMod(0.8F).alwaysEat();
        switch (c) {
            case KITTY -> b.effect(() -> new MobEffectInstance(MobEffects.LUCK, 1200), 1.0F);
            case MELODY -> b.effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 160), 1.0F);
            case KUROMI -> b.effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200), 1.0F);
            case CINNAMOROLL -> b.effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200), 1.0F);
            case PURIN -> b.effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1), 1.0F);
        }
        return b.build();
    }

    private static Item charmFor(SanrioCharacter c) {
        Item.Properties p = new Item.Properties().stacksTo(1);
        return switch (c) {
            case KITTY -> new CharmItem(p, () -> MobEffects.LUCK, 0, c.charmTip);
            case MELODY -> new CharmItem(p, () -> MobEffects.REGENERATION, 0, c.charmTip);
            case KUROMI -> new CharmItem(p, () -> MobEffects.DAMAGE_BOOST, 0, c.charmTip);
            case CINNAMOROLL -> new CharmItem(p, () -> MobEffects.SLOW_FALLING, 0, c.charmTip);
            case PURIN -> new CharmItem(p, () -> MobEffects.ABSORPTION, 0, c.charmTip);
        };
    }
}
