package com.sanriofriends.client;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SanrioFriends.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {}

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            event.registerLayerDefinition(SanrioModel.layer(c), () -> SanrioModel.createBodyLayer(c));
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            event.registerEntityRenderer(ModEntities.type(c).get(), ctx -> new SanrioRenderer(ctx, c));
        }
    }
}
