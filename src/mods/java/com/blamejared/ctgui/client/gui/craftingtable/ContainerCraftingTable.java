package com.blamejared.ctgui.client.gui.craftingtable;

import com.blamejared.ctgui.api.ContainerBase;
import com.blamejared.ctgui.api.SlotRecipe;
import com.blamejared.ctgui.api.SlotRecipeOutput;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;

public class ContainerCraftingTable extends ContainerBase {
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
   public IInventory craftResult = new InventoryCraftResult();

   public ContainerCraftingTable(InventoryPlayer invPlayer) {
      this.addSlotToContainer(new SlotRecipeOutput(this.craftResult, 0, 124, 35));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new SlotRecipe(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
         }
      }

      for(int x = 0; x < 9; ++x) {
         this.addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 142));
      }

      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 84 + y * 18));
         }
      }

   }
}
