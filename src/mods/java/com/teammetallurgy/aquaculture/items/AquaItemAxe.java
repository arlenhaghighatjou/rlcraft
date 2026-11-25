package com.teammetallurgy.aquaculture.items;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.Item.ToolMaterial;

public class AquaItemAxe extends ItemAxe {
   public AquaItemAxe(ToolMaterial toolMaterial) {
      super(toolMaterial, 8.0F, -3.0F);
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      return this;
   }
}
