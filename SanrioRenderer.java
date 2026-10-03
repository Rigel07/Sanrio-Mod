package com.sanriofriends.client;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.entity.SanrioEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SanrioRenderer extends MobRenderer<SanrioEntity, SanrioModel> {
    private final ResourceLocation texture;

    public SanrioRenderer(EntityRendererProvider.Context context, SanrioCharacter character) {
        super(context, new SanrioModel(context.bakeLayer(SanrioModel.layer(character)), character), 0.3F);
        this.texture = new ResourceLocation(SanrioFriends.MOD_ID, "textures/entity/" + character.id + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(SanrioEntity entity) {
        return this.texture;
    }
}
