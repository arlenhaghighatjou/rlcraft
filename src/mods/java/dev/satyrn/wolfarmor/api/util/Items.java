package dev.satyrn.wolfarmor.api.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolderRegistry;
import dev.satyrn.wolfarmor.api.item.IItemWolfArmor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class Items {
   public static Item LEATHER_WOLF_ARMOR = null;
   public static Item CHAINMAIL_WOLF_ARMOR = null;
   public static Item IRON_WOLF_ARMOR = null;
   public static Item GOLD_WOLF_ARMOR = null;
   public static Item DIAMOND_WOLF_ARMOR = null;

   public Items() {
   }

   public static boolean isValidWolfArmor(@Nonnull ItemStack armorItemStack) {
      return armorItemStack.isEmpty() || isValidWolfArmor(armorItemStack.getItem());
   }

   public static boolean isValidWolfArmor(@Nullable Item armorItem) {
      return armorItem instanceof IItemWolfArmor;
   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("wolfarmor:leather_wolf_armor"), v -> LEATHER_WOLF_ARMOR = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("wolfarmor:chainmail_wolf_armor"), v -> CHAINMAIL_WOLF_ARMOR = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("wolfarmor:iron_wolf_armor"), v -> IRON_WOLF_ARMOR = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("wolfarmor:gold_wolf_armor"), v -> GOLD_WOLF_ARMOR = v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("wolfarmor:diamond_wolf_armor"), v -> DIAMOND_WOLF_ARMOR = v);
   }
}
