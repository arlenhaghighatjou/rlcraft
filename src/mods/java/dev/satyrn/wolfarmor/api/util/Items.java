package dev.satyrn.wolfarmor.api.util;

import dev.satyrn.wolfarmor.api.item.IItemWolfArmor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("wolfarmor")
public abstract class Items {
   @ObjectHolder("leather_wolf_armor")
   public static final Item LEATHER_WOLF_ARMOR = null;
   @ObjectHolder("chainmail_wolf_armor")
   public static final Item CHAINMAIL_WOLF_ARMOR = null;
   @ObjectHolder("iron_wolf_armor")
   public static final Item IRON_WOLF_ARMOR = null;
   @ObjectHolder("gold_wolf_armor")
   public static final Item GOLD_WOLF_ARMOR = null;
   @ObjectHolder("diamond_wolf_armor")
   public static final Item DIAMOND_WOLF_ARMOR = null;

   public Items() {
   }

   public static boolean isValidWolfArmor(@Nonnull ItemStack armorItemStack) {
      return armorItemStack.isEmpty() || isValidWolfArmor(armorItemStack.getItem());
   }

   public static boolean isValidWolfArmor(@Nullable Item armorItem) {
      return armorItem instanceof IItemWolfArmor;
   }
}
