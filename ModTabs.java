package com.sanriofriends.registry;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SanrioFriends.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.sanriofriends"))
                    .icon(() -> new ItemStack(ModItems.FOOD.get(SanrioCharacter.KITTY).get()))
                    .displayItems((params, out) -> {
                        for (SanrioCharacter c : SanrioCharacter.values()) {
                            out.accept(ModItems.EGG.get(c).get());
                        }
                        for (SanrioCharacter c : SanrioCharacter.values()) {
                            out.accept(ModItems.FOOD.get(c).get());
                        }
                        for (SanrioCharacter c : SanrioCharacter.values()) {
                            out.accept(ModItems.CHARM.get(c).get());
                        }
                        for (SanrioCharacter c : SanrioCharacter.values()) {
                            out.accept(ModBlocks.PLUSH.get(c).get());
                        }
                        out.accept(ModBlocks.PINK_HEART.get());
                        out.accept(ModBlocks.CLOUD.get());
                        out.accept(ModBlocks.PUDDING.get());
                        out.accept(ModBlocks.SKULL.get());
                        out.accept(ModBlocks.STAR_LAMP.get());
                    })
                    .build());

    private ModTabs() {}
}
