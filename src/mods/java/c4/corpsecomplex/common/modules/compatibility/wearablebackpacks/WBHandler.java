package c4.corpsecomplex.common.modules.compatibility.wearablebackpacks;

import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathInventoryHandler;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHelper;
import java.util.Random;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.misc.BackpackCapability;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class WBHandler extends DeathInventoryHandler {
   private static final String MOD_ID = "wearablebackpacks";
   private IBackpack playerBackpack;

   public WBHandler(EntityPlayer player) {
      super(player, "wearablebackpacks");
      this.playerBackpack = (IBackpack)player.getCapability(BackpackCapability.CAPABILITY, (EnumFacing)null);
   }

   public boolean checkToStore(int slot) {
      return WBModule.keepBackpack;
   }

   public void storeInventory() {
      if (this.playerBackpack != null) {
         ItemStack stack;
         if (BackpackHelper.equipAsChestArmor) {
            stack = this.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         } else {
            stack = this.playerBackpack.getStack();
         }

         if (!stack.isEmpty()) {
            Random generator = new Random();
            boolean essential = DeathStackHelper.isEssential(stack);
            boolean cursed = !essential && DeathStackHelper.isCursed(stack);
            boolean store = this.checkToStore(0) && !cursed || essential;
            if (!store && EnchantmentModule.registerEnchant) {
               int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentModule.soulbound, stack);
               if (level != 0) {
                  store = essential = DeathStackHelper.handleSoulbound(stack, level);
                  cursed = !essential && cursed;
               }
            }

            if (cursed && InventoryModule.destroyCursed) {
               BackpackHelper.setEquippedBackpack(this.player, ItemStack.EMPTY, (IBackpackData)null);
            } else {
               if ((InventoryModule.dropLoss > 0.0D || InventoryModule.keptLoss > 0.0D) && (!BackpackHelper.equipAsChestArmor || store)) {
                  DeathStackHelper.loseDurability(this.player, stack, store);
               }

               if (!stack.isEmpty()) {
                  if (store) {
                     if (!essential && generator.nextDouble() < InventoryModule.randomDrop) {
                        if (generator.nextDouble() < InventoryModule.randomDestroy) {
                           BackpackHelper.setEquippedBackpack(this.player, ItemStack.EMPTY, (IBackpackData)null);
                           return;
                        }

                        return;
                     }

                     NBTTagCompound storage = (NBTTagCompound)BackpackCapability.CAPABILITY.writeNBT(this.playerBackpack, (EnumFacing)null);
                     if (storage != null && BackpackHelper.equipAsChestArmor) {
                        NBTTagCompound compound = NbtUtils.writeItem(stack);
                        storage.setTag("stack", compound);
                     }

                     this.deathInventory.addStorage(this.modid, storage);
                     BackpackHelper.setEquippedBackpack(this.player, ItemStack.EMPTY, (IBackpackData)null);
                  }

               }
            }
         }
      }
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound storage = oldDeathInventory.getStorage("wearablebackpacks");
      if (storage != null && this.playerBackpack != null) {
         BackpackCapability.CAPABILITY.readNBT(this.playerBackpack, (EnumFacing)null, storage);
      }
   }
}
