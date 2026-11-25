package dev.satyrn.wolfarmor.api.item;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public interface IWolfArmorMaterial {
   double MAX_VANILLA_ARMOR_VALUE = 20.0D;

   @Nonnull
   String getName();

   @Nonnegative
   int getDurability();

   @Nonnegative
   double getDamageReductionAmount();

   @Nonnegative
   int getEnchantability();

   boolean getCanBeDyed();

   int getDefaultColor();

   @Nonnull
   SoundEvent getEquipSound();

   boolean getHasOverlay();

   @Nullable
   Item getRepairItem();

   @Nonnegative
   float getToughness();
}
