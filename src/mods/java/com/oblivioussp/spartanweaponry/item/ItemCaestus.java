package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemCaestus extends ItemWeaponBase {
   public ItemCaestus(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseCaestus, ConfigHandler.damageMultiplierCaestus, (double)ConfigHandler.speedCaestus, WeaponProperties.QUICK_STRIKE);
   }

   public ItemStack getRepairItemStack() {
      return this.materialEx.getUnlocName() == ToolMaterialEx.IRON.getUnlocName() ? new ItemStack(Items.IRON_INGOT, 1, 32767) : new ItemStack(Items.LEATHER, 1, 32767);
   }
}
