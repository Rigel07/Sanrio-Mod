package com.sanriofriends;

import com.sanriofriends.entity.SanrioEntity;
import com.sanriofriends.registry.ModBlocks;
import com.sanriofriends.registry.ModEntities;
import com.sanriofriends.registry.ModItems;
import com.sanriofriends.registry.ModTabs;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Sanrio Friends: mod no oficial hecho por fans. Los personajes pertenecen a Sanrio Co., Ltd. */
@Mod(SanrioFriends.MOD_ID)
public class SanrioFriends {
    public static final String MOD_ID = "sanriofriends";

    public SanrioFriends() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModTabs.TABS.register(bus);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        private ModEvents() {}

        @SubscribeEvent
        public static void onAttributes(EntityAttributeCreationEvent event) {
            for (SanrioCharacter c : SanrioCharacter.values()) {
                event.put(ModEntities.type(c).get(), SanrioEntity.createAttributes().build());
            }
        }
    }
}
