package noppes.vc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SlotNpcCrafting extends SlotCrafting {
   private final InventoryCrafting craftMatrix;

   public SlotNpcCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition) {
      super(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition);
      this.craftMatrix = craftingInventory;
   }

   public ItemStack onTake(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
      FMLCommonHandler.instance().firePlayerCraftingEvent(p_82870_1_, p_82870_2_, this.craftMatrix);
      this.onCrafting(p_82870_2_);

      for(int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
         ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            this.craftMatrix.decrStackSize(i, 1);
            if (itemstack1.getItem().hasContainerItem(itemstack1)) {
               ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);
               if ((itemstack2.isEmpty() || !itemstack2.isItemStackDamageable() || itemstack2.getItemDamage() <= itemstack2.getMaxDamage()) && !p_82870_1_.inventory.addItemStackToInventory(itemstack2)) {
                  if (this.craftMatrix.getStackInSlot(i).isEmpty()) {
                     this.craftMatrix.setInventorySlotContents(i, itemstack2);
                  } else {
                     p_82870_1_.dropItem(itemstack2, false);
                  }
               }
            }
         }
      }

      return p_82870_2_;
   }
}
