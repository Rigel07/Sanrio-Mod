package com.sanriofriends.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** Amuleto de un personaje: da un efecto pasivo mientras está en la barra rápida (o en la mano izquierda). */
public class CharmItem extends Item {
    private final Supplier<MobEffect> effect;
    private final int amplifier;
    private final String tip;

    public CharmItem(Properties properties, Supplier<MobEffect> effect, int amplifier, String tip) {
        super(properties);
        this.effect = effect;
        this.amplifier = amplifier;
        this.tip = tip;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player && slot < 9 && player.tickCount % 40 == 0) {
            player.addEffect(new MobEffectInstance(this.effect.get(), 140, this.amplifier, true, false, true));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(this.tip).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
