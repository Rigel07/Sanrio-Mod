package com.sanriofriends.registry;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.entity.CinnamorollEntity;
import com.sanriofriends.entity.SanrioEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SanrioFriends.MOD_ID);

    private static final Map<SanrioCharacter, RegistryObject<EntityType<SanrioEntity>>> TYPES = new EnumMap<>(SanrioCharacter.class);

    static {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            TYPES.put(c, register(c));
        }
    }

    private ModEntities() {}

    public static RegistryObject<EntityType<SanrioEntity>> type(SanrioCharacter c) {
        return TYPES.get(c);
    }

    private static EntityType.EntityFactory<SanrioEntity> factory(SanrioCharacter c) {
        if (c == SanrioCharacter.CINNAMOROLL) {
            return CinnamorollEntity::new;
        }
        return (type, level) -> new SanrioEntity(type, level, c);
    }

    private static RegistryObject<EntityType<SanrioEntity>> register(SanrioCharacter c) {
        EntityType.EntityFactory<SanrioEntity> factory = factory(c);
        float height = c == SanrioCharacter.CINNAMOROLL ? 1.0F : 1.2F;
        return ENTITIES.register(c.id, () -> EntityType.Builder.of(factory, MobCategory.CREATURE)
                .sized(0.6F, height)
                .clientTrackingRange(10)
                .build(new ResourceLocation(SanrioFriends.MOD_ID, c.id).toString()));
    }
}
