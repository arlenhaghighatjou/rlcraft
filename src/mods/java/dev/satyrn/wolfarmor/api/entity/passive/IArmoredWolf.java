package dev.satyrn.wolfarmor.api.entity.passive;

import dev.satyrn.wolfarmor.api.entity.IFoodStatsCreature;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IArmoredWolf extends IFoodStatsCreature, IInventoryChangedListener {
   @Nullable
   Item getChestType();

   void setChestType(@Nonnull ItemStack var1);

   boolean getHasChest();

   void setHasChest(boolean var1);

   int getMaxSizeInventory();

   @Nonnull
   InventoryBasic getInventory();

   @Nonnull
   ItemStack getArmorItemStack();

   void setArmorItemStack(@Nonnull ItemStack var1);

   boolean getHasArmor();

   boolean canEquipItem(@Nonnull ItemStack var1);

   void equipArmor(@Nonnull ItemStack var1);

   void setInventoryItem(int var1, @Nonnull ItemStack var2);

   void dropEquipment();

   void dropChest();

   void dropInventoryContents();
}
