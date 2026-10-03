package com.sanriofriends.registry;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.block.PlushBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SanrioFriends.MOD_ID);

    public static final RegistryObject<Block> PINK_HEART = reg("pink_heart_block",
            () -> new Block(props(MapColor.COLOR_PINK, SoundType.WOOL)));
    public static final RegistryObject<Block> CLOUD = reg("cloud_block",
            () -> new Block(props(MapColor.SNOW, SoundType.WOOL)));
    public static final RegistryObject<Block> PUDDING = reg("pudding_block",
            () -> new Block(props(MapColor.COLOR_YELLOW, SoundType.SLIME_BLOCK)));
    public static final RegistryObject<Block> SKULL = reg("kuromi_skull_block",
            () -> new Block(props(MapColor.COLOR_BLACK, SoundType.STONE)));
    public static final RegistryObject<Block> STAR_LAMP = reg("star_lamp",
            () -> new Block(props(MapColor.COLOR_YELLOW, SoundType.GLASS).lightLevel(s -> 14)));

    public static final Map<SanrioCharacter, RegistryObject<Block>> PLUSH = new EnumMap<>(SanrioCharacter.class);

    static {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            PLUSH.put(c, reg(c.plushId, () -> new PlushBlock(props(MapColor.COLOR_PINK, SoundType.WOOL).noOcclusion())));
        }
    }

    private ModBlocks() {}

    private static BlockBehaviour.Properties props(MapColor color, SoundType sound) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(0.8F).sound(sound);
    }

    private static RegistryObject<Block> reg(String name, Supplier<Block> sup) {
        RegistryObject<Block> block = BLOCKS.register(name, sup);
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
