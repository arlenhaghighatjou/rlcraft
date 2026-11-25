package crafttweaker.mc1120.recipes;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.ICraftingInventory;
import crafttweaker.mc1120.player.MCPlayer;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public class MCCraftingInventory implements ICraftingInventory {
   private final IInventory inventory;
   private final IPlayer player;
   private final EntityPlayer playerOrig;
   private int width;
   private int height;
   private IItemStack[] stacks;
   private ItemStack[] original;
   private int stackCount;

   private MCCraftingInventory(InventoryCrafting inventory) {
      this.inventory = inventory;
      this.width = this.height = (int)Math.sqrt((double)inventory.getSizeInventory());
      this.stacks = new IItemStack[this.width * this.height];
      this.original = new ItemStack[this.stacks.length];
      this.stackCount = 0;
      this.update();
      Container container = inventory.eventHandler;
      if (container != null) {
         List<Slot> slots = container.inventorySlots;
         if (!slots.isEmpty() && slots.get(0) instanceof SlotCrafting) {
            SlotCrafting slotCrafting = (SlotCrafting)slots.get(0);
            this.playerOrig = slotCrafting.player;
            this.player = CraftTweakerMC.getIPlayer(this.playerOrig);
         } else {
            this.playerOrig = null;
            this.player = null;
         }
      } else {
         this.playerOrig = null;
         this.player = null;
      }

   }

   public MCCraftingInventory(IInventory inventory, EntityPlayer player) {
      this.inventory = inventory;
      this.width = this.height = (int)Math.sqrt((double)inventory.getSizeInventory());
      this.stacks = new IItemStack[this.width * this.height];
      this.original = new ItemStack[this.stacks.length];
      this.stackCount = 0;
      this.update();
      this.playerOrig = player;
      this.player = player == null ? null : new MCPlayer(player);
   }

   public static MCCraftingInventory get(InventoryCrafting inventory) {
      return new MCCraftingInventory(inventory);
   }

   public static MCCraftingInventory get(IInventory inventory, EntityPlayer player) {
      return new MCCraftingInventory(inventory, player);
   }

   private void update() {
      if (this.inventory.getSizeInventory() != this.original.length) {
         this.width = this.height = (int)Math.sqrt((double)this.inventory.getSizeInventory());
         this.stacks = new IItemStack[this.inventory.getSizeInventory()];
         this.original = new ItemStack[this.stacks.length];
         this.stackCount = 0;
      }

      for(int i = 0; i < this.inventory.getSizeInventory(); ++i) {
         if (this.changed(i)) {
            this.original[i] = this.inventory.getStackInSlot(i);
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
               if (this.stacks[i] == null) {
                  ++this.stackCount;
               }

               this.stacks[i] = CraftTweakerMC.getIItemStack(this.original[i]);
            } else {
               if (this.stacks[i] != null) {
                  --this.stackCount;
               }

               this.stacks[i] = null;
            }
         }
      }

   }

   public IPlayer getPlayer() {
      return this.player;
   }

   public int getSize() {
      return this.width * this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getStackCount() {
      return this.stackCount;
   }

   public IItemStack getStack(int i) {
      return this.stacks[i];
   }

   public IItemStack getStack(int row, int column) {
      return this.stacks[row * this.width + column];
   }

   public void setStack(int row, int column, IItemStack stack) {
      int ix = row * this.width + column;
      if (stack != this.stacks[ix]) {
         if (stack == null) {
            --this.stackCount;
            this.inventory.setInventorySlotContents(ix, ItemStack.EMPTY);
         } else {
            this.inventory.setInventorySlotContents(ix, CraftTweakerMC.getItemStack(stack));
            if (this.stacks[ix] == null) {
               ++this.stackCount;
            }
         }

         this.stacks[ix] = stack;
      }

   }

   public void setStack(int i, IItemStack stack) {
      if (stack != this.stacks[i]) {
         if (stack == null) {
            --this.stackCount;
            this.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
         } else {
            this.inventory.setInventorySlotContents(i, CraftTweakerMC.getItemStack(stack));
            if (this.stacks[i] == null) {
               ++this.stackCount;
            }
         }

         this.stacks[i] = stack;
      }

   }

   public IItemStack[][] getItems() {
      return new IItemStack[0][];
   }

   public IItemStack[] getItemArray() {
      return new IItemStack[0];
   }

   public Object getInternal() {
      return this.inventory;
   }

   private boolean changed(int i) {
      return this.original[i] != this.inventory.getStackInSlot(i) || !this.original[i].isEmpty() && this.stacks[i].getAmount() != this.original[i].getCount();
   }
}
