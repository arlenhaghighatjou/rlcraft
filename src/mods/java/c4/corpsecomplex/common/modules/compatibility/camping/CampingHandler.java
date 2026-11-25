package c4.corpsecomplex.common.modules.compatibility.camping;

import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.enchantment.EnchantmentModule;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathInventoryHandler;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHelper;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class CampingHandler extends DeathInventoryHandler {
   private static final String MOD_ID = "camping";
   private NonNullList<ItemStack> campingSlots;

   public CampingHandler(EntityPlayer player) {
      super(player, "camping");
      this.campingSlots = this.getCampingSlots(player);
   }

   public void storeInventory() {
      NonNullList<ItemStack> storedStacks = NonNullList.withSize(4, ItemStack.EMPTY);
      if (this.campingSlots != null) {
         Random generator = new Random();

         for(int i = 0; i < this.campingSlots.size(); ++i) {
            ItemStack stack = (ItemStack)this.campingSlots.get(i);
            if (!stack.isEmpty()) {
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
                  this.campingSlots.set(i, ItemStack.EMPTY);
               } else {
                  if (InventoryModule.dropLoss > 0.0D || InventoryModule.keptLoss > 0.0D) {
                     DeathStackHelper.loseDurability(this.player, stack, store);
                  }

                  if (stack.isEmpty()) {
                     return;
                  }

                  if (store && !essential && generator.nextDouble() < InventoryModule.randomDrop) {
                     if (generator.nextDouble() < InventoryModule.randomDestroy) {
                        this.campingSlots.set(i, ItemStack.EMPTY);
                     }
                  } else {
                     storedStacks.set(i, stack.copy());
                     this.campingSlots.set(i, ItemStack.EMPTY);
                  }
               }
            }
         }

         this.player.getEntityData().setTag("campInv", this.stacksToData(this.campingSlots));
         NBTTagCompound tag = this.player.getEntityData().getCompoundTag("PlayerPersisted");
         tag.setTag("campInv", this.stacksToData(storedStacks));
         this.player.getEntityData().setTag("PlayerPersisted", tag);
      }
   }

   private NBTTagCompound stacksToData(NonNullList<ItemStack> stacks) {
      NBTTagCompound data = new NBTTagCompound();
      NBTTagList list = new NBTTagList();

      for(int i = 0; i < stacks.size(); ++i) {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setByte("slotIndex", (byte)i);
         list.appendTag(((ItemStack)stacks.get(i)).writeToNBT(compound));
      }

      data.setTag("items", list);
      return data;
   }

   private NonNullList<ItemStack> getCampingSlots(EntityPlayer player) {
      NonNullList<ItemStack> stacks = NonNullList.withSize(4, ItemStack.EMPTY);
      if (player.getEntityData().hasKey("campInv")) {
         NBTTagList taglist = player.getEntityData().getCompoundTag("campInv").getTagList("items", 10);
         if (!taglist.hasNoTags()) {
            for(int i = 0; i < taglist.tagCount(); ++i) {
               NBTTagCompound compound = taglist.getCompoundTagAt(i);
               stacks.set(compound.getByte("slotIndex"), new ItemStack(compound));
            }
         }
      }

      return stacks;
   }

   public boolean checkToStore(int slot) {
      return CampingModule.keepCamping;
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
   }
}
