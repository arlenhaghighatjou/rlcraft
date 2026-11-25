package com.endertech.minecraft.forge.units;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IUnitWithMeshDefinition {
   @SideOnly(Side.CLIENT)
   @Nullable
   default ItemMeshDefinition getItemMeshDefinition() {
      return (stack) -> {
         return this.getVariantFor(stack).toModelResLoc();
      };
   }

   UnitVariant getVariantFor(ItemStack var1);
}
