package crafttweaker.mc1120.recipes;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.ICraftingInventory;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;

public class MCCraftingInventorySquared implements ICraftingInventory {
   private final IInventory inventory;
   private final IPlayer player;
   private final int height;
   private final int width;

   protected MCCraftingInventorySquared(IInventory inventory) {
      this(inventory, (IPlayer)null);
   }

   public MCCraftingInventorySquared(IInventory inventory, IPlayer player) {
      this.inventory = inventory;
      this.height = this.width = (int)Math.sqrt((double)inventory.getSizeInventory());
      this.player = player == null ? getPlayerFromInventory(inventory) : player;
   }

   private static IPlayer getPlayerFromInventory(IInventory inventory) {
      if (inventory instanceof InventoryCrafting) {
         InventoryCrafting inventoryCrafting = (InventoryCrafting)inventory;
         Container eventHandler = inventoryCrafting.eventHandler;
         if (eventHandler != null) {
            for(Slot slot : eventHandler.inventorySlots) {
               if (slot instanceof SlotCrafting) {
                  return CraftTweakerMC.getIPlayer(((SlotCrafting)slot).player);
               }
            }
         }
      }

      return null;
   }

   public static MCCraftingInventorySquared get(IInventory inventory) {
      return new MCCraftingInventorySquared(inventory);
   }

   public IPlayer getPlayer() {
      return this.player;
   }

   public int getSize() {
      return this.inventory.getSizeInventory();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getStackCount() {
      int count = 0;

      for(int slot = 0; slot < this.getSize(); ++slot) {
         if (!this.inventory.getStackInSlot(slot).isEmpty()) {
            ++count;
         }
      }

      return count;
   }

   public IItemStack getStack(int i) {
      return CraftTweakerMC.getIItemStack(this.inventory.getStackInSlot(i));
   }

   public IItemStack getStack(int row, int column) {
      return this.getStack(column + row * this.getWidth());
   }

   public void setStack(int row, int column, IItemStack stack) {
      this.setStack(column + row * this.getWidth(), stack);
   }

   public void setStack(int i, IItemStack stack) {
      this.inventory.setInventorySlotContents(i, CraftTweakerMC.getItemStack(stack));
   }

   public IItemStack[][] getItems() {
      IItemStack[][] output = new IItemStack[this.getHeight()][this.getWidth()];
      IItemStack[] oneDimensional = this.getItemArray();

      for(int row = 0; row < this.getHeight(); ++row) {
         for(int column = 0; column < this.getWidth(); ++column) {
            output[row][column] = oneDimensional[row * this.getWidth() + column];
         }
      }

      return output;
   }

   public IItemStack[] getItemArray() {
      IItemStack[] output = new IItemStack[this.getSize()];

      for(int slot = 0; slot < this.getSize(); ++slot) {
         output[slot] = CraftTweakerMC.getIItemStack(this.inventory.getStackInSlot(slot));
      }

      return output;
   }

   public Object getInternal() {
      return this.inventory;
   }
}
