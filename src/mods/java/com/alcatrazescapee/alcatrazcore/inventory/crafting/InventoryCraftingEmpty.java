package com.alcatrazescapee.alcatrazcore.inventory.crafting;

import com.alcatrazescapee.alcatrazcore.inventory.container.ContainerEmpty;
import net.minecraft.inventory.InventoryCrafting;

public class InventoryCraftingEmpty extends InventoryCrafting {
   private static final ContainerEmpty nullContainer = new ContainerEmpty();

   public InventoryCraftingEmpty(int width, int height) {
      super(nullContainer, width, height);
   }
}
