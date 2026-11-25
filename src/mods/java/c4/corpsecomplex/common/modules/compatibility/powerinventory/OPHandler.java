package c4.corpsecomplex.common.modules.compatibility.powerinventory;

import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHelper;
import com.lothrazar.powerinventory.inventory.InventoryOverpowered;
import com.lothrazar.powerinventory.util.UtilPlayerInventoryFilestorage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class OPHandler extends DeathStackHandler {
   private static final String MOD_ID = "powerinventory";
   private InventoryOverpowered playerInventory;

   public OPHandler(EntityPlayer player) {
      super(player, "powerinventory");
      this.playerInventory = UtilPlayerInventoryFilestorage.getPlayerInventory(player);
      this.setSize(this.playerInventory.getSizeInventory());
   }

   public void storeInventory() {
      for(int index = 0; index < this.storage.getSlots(); ++index) {
         ItemStack stack = this.getStackInSlot(index);
         if (!stack.isEmpty()) {
            boolean essential = DeathStackHelper.isEssential(stack);
            boolean cursed = !essential && DeathStackHelper.isCursed(stack);
            boolean store = this.checkToStore(index) && !cursed || essential;
            if (!store && EnchantmentModule.registerEnchant) {
               int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentModule.soulbound, stack);
               if (level != 0) {
                  store = essential = DeathStackHelper.handleSoulbound(stack, level);
                  cursed = !essential && cursed;
               }
            }

            if (cursed && InventoryModule.destroyCursed) {
               DeathStackHelper.destroyStack(stack);
            } else {
               if (InventoryModule.dropLoss > 0.0D || InventoryModule.keptLoss > 0.0D) {
                  DeathStackHelper.loseDurability(this.player, stack, store);
               }

               if (InventoryModule.dropDrain > 0.0D || InventoryModule.keptDrain > 0.0D) {
                  DeathStackHelper.loseEnergy(stack, store);
               }

               if (!stack.isEmpty()) {
                  if (store) {
                     if (essential || InventoryModule.randomDrop <= 0.0D) {
                        continue;
                     }

                     int dropAmount = 0;
                     dropAmount = dropAmount + DeathStackHelper.randomlyDrop(stack);
                     this.player.dropItem(stack.splitStack(dropAmount), false);
                  } else {
                     this.playerInventory.dropStackInSlot(this.player, index);
                     this.playerInventory.setInventorySlotContents(index, ItemStack.EMPTY);
                  }

                  if (!stack.isEmpty() && InventoryModule.randomDestroy > 0.0D) {
                     DeathStackHelper.randomlyDestroy(stack);
                  }
               }
            }
         }
      }

   }

   public boolean checkToStore(int slot) {
      return OPModule.keepOP;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.playerInventory.getStackInSlot(slot);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
   }
}
