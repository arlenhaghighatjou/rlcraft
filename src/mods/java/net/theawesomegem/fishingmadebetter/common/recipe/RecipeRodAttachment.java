package net.theawesomegem.fishingmadebetter.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.theawesomegem.fishingmadebetter.common.item.attachment.bobber.ItemBobber;
import net.theawesomegem.fishingmadebetter.common.item.attachment.bobber.ItemBobberBasic;
import net.theawesomegem.fishingmadebetter.common.item.attachment.hook.ItemHook;
import net.theawesomegem.fishingmadebetter.common.item.attachment.hook.ItemHookBasic;
import net.theawesomegem.fishingmadebetter.common.item.attachment.reel.ItemReel;
import net.theawesomegem.fishingmadebetter.common.item.attachment.reel.ItemReelBasic;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;

public class RecipeRodAttachment extends Impl<IRecipe> implements IRecipe {
   public RecipeRodAttachment() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemFishingRod = inv.getStackInSlot(slots[0].intValue()).copy();
         ItemStack itemReel = slots[1].intValue() != -1 ? inv.getStackInSlot(slots[1].intValue()).copy() : null;
         ItemStack itemBobber = slots[2].intValue() != -1 ? inv.getStackInSlot(slots[2].intValue()).copy() : null;
         ItemStack itemHook = slots[3].intValue() != -1 ? inv.getStackInSlot(slots[3].intValue()).copy() : null;
         if (itemReel != null) {
            ItemBetterFishingRod.setReelItem(itemFishingRod, (ItemReel)itemReel.getItem());
            ItemBetterFishingRod.setReelDamage(itemFishingRod, itemReel.getItemDamage());
         }

         if (itemBobber != null) {
            ItemBetterFishingRod.setBobberItem(itemFishingRod, (ItemBobber)itemBobber.getItem());
            ItemBetterFishingRod.setBobberDamage(itemFishingRod, itemBobber.getItemDamage());
         }

         if (itemHook != null) {
            ItemBetterFishingRod.setHookItem(itemFishingRod, (ItemHook)itemHook.getItem());
            ItemBetterFishingRod.setHookDamage(itemFishingRod, itemHook.getItemDamage());
         }

         if (itemReel == null && itemBobber == null && itemHook == null) {
            ItemBetterFishingRod.removeReelItem(itemFishingRod);
            ItemBetterFishingRod.removeBobberItem(itemFishingRod);
            ItemBetterFishingRod.removeHookItem(itemFishingRod);
         }

         return itemFishingRod;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      ItemStack itemFishingRod = inv.getStackInSlot(slots[0].intValue()).copy();
      ItemStack itemReel = slots[1].intValue() != -1 ? inv.getStackInSlot(slots[1].intValue()).copy() : null;
      ItemStack itemBobber = slots[2].intValue() != -1 ? inv.getStackInSlot(slots[2].intValue()).copy() : null;
      ItemStack itemHook = slots[3].intValue() != -1 ? inv.getStackInSlot(slots[3].intValue()).copy() : null;
      ItemStack oldReel = ItemBetterFishingRod.hasReelItem(itemFishingRod) ? new ItemStack(ItemBetterFishingRod.getReelItem(itemFishingRod)) : null;
      if (oldReel != null) {
         oldReel.setItemDamage(ItemBetterFishingRod.getReelDamage(itemFishingRod));
      }

      ItemStack oldBobber = ItemBetterFishingRod.hasBobberItem(itemFishingRod) ? new ItemStack(ItemBetterFishingRod.getBobberItem(itemFishingRod)) : null;
      if (oldBobber != null) {
         oldBobber.setItemDamage(ItemBetterFishingRod.getBobberDamage(itemFishingRod));
      }

      ItemStack oldHook = ItemBetterFishingRod.hasHookItem(itemFishingRod) ? new ItemStack(ItemBetterFishingRod.getHookItem(itemFishingRod)) : null;
      if (oldHook != null) {
         oldHook.setItemDamage(ItemBetterFishingRod.getHookDamage(itemFishingRod));
      }

      if (itemReel != null || itemBobber != null || itemHook != null) {
         if (itemReel == null && oldReel != null) {
            oldReel = null;
         }

         if (itemBobber == null && oldBobber != null) {
            oldBobber = null;
         }

         if (itemHook == null && oldHook != null) {
            oldHook = null;
         }
      }

      NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < ret.size(); ++i) {
         if (oldReel != null) {
            ret.set(i, oldReel);
            oldReel = null;
         } else if (oldBobber != null) {
            ret.set(i, oldBobber);
            oldBobber = null;
         } else {
            if (oldHook == null) {
               break;
            }

            ret.set(i, oldHook);
            oldHook = null;
         }
      }

      return ret;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 4;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numRod = 0;
      int numReel = 0;
      int numBobber = 0;
      int numHook = 0;
      int slotRod = -1;
      int slotReel = -1;
      int slotBobber = -1;
      int slotHook = -1;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      Iterator var14 = occupiedSlots.iterator();

      while(true) {
         if (var14.hasNext()) {
            int i = ((Integer)var14.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.isEmpty()) {
               return null;
            }

            if (itemStack.getItem() instanceof ItemBetterFishingRod) {
               ++numRod;
               slotRod = i;
               continue;
            }

            if (itemStack.getItem() instanceof ItemReel && !(itemStack.getItem() instanceof ItemReelBasic)) {
               ++numReel;
               slotReel = i;
               continue;
            }

            if (itemStack.getItem() instanceof ItemBobber && !(itemStack.getItem() instanceof ItemBobberBasic)) {
               ++numBobber;
               slotBobber = i;
               continue;
            }

            if (itemStack.getItem() instanceof ItemHook && !(itemStack.getItem() instanceof ItemHookBasic)) {
               ++numHook;
               slotHook = i;
               continue;
            }

            return null;
         }

         Integer[] slots = new Integer[4];
         slots[0] = slotRod;
         slots[1] = slotReel;
         slots[2] = slotBobber;
         slots[3] = slotHook;
         return numRod == 1 && numReel < 2 && numBobber < 2 && numHook < 2 ? slots : null;
      }
   }
}
