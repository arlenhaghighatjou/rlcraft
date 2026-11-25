package com.oblivioussp.spartanweaponry.util;

import baubles.api.BaublesApi;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.init.OreDictionarySW;
import com.oblivioussp.spartanweaponry.item.ItemBolt;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class QuiverHelper {
   public static final int SLOT_BODY = 5;
   public static List<QuiverHelper.IQuiverInfo> info = new ArrayList();

   public QuiverHelper() {
   }

   public static ItemStack findFirstOfType(EntityPlayer player, QuiverHelper.IQuiverInfo info) {
      if (ModSpartanWeaponry.isBaublesLoaded) {
         ItemStack bauble = findFromBauble(player);
         if (!bauble.isEmpty() && info.isQuiver(bauble)) {
            return bauble;
         }
      }

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = player.inventory.getStackInSlot(i);
         if (!stack.isEmpty() && info.isQuiver(stack)) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static ItemStack findFirst(EntityPlayer player) {
      if (ModSpartanWeaponry.isBaublesLoaded) {
         ItemStack bauble = findFromBauble(player);
         if (!bauble.isEmpty() && OreDictionarySW.matches("spartanweaponry:quivers", bauble)) {
            return bauble;
         }
      }

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = player.inventory.getStackInSlot(i);
         if (!stack.isEmpty() && OreDictionarySW.matches("spartanweaponry:quivers", stack)) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public static List<ItemStack> findValidQuivers(EntityPlayer player) {
      List<ItemStack> result = new ArrayList();
      if (ModSpartanWeaponry.isBaublesLoaded) {
         ItemStack bauble = BaublesApi.getBaublesHandler(player).getStackInSlot(5);
         if (!bauble.isEmpty() && OreDictionarySW.matches("spartanweaponry:quivers", bauble)) {
            result.add(bauble);
         }
      }

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = player.inventory.getStackInSlot(i);
         if (!stack.isEmpty() && OreDictionarySW.matches("spartanweaponry:quivers", stack)) {
            result.add(stack);
         }
      }

      return result;
   }

   public static ItemStack findFromBauble(EntityPlayer player) {
      return BaublesApi.getBaublesHandler(player).getStackInSlot(5);
   }

   public static boolean isInBaublesSlot(EntityPlayer player, ItemStack stack) {
      return BaublesApi.getBaublesHandler(player).getStackInSlot(5).isItemEqual(stack);
   }

   static {
      info.add(new QuiverHelper.IQuiverInfo() {
         public boolean isQuiver(ItemStack stack) {
            return OreDictionarySW.matches("spartanweaponry:bolt_quivers", stack);
         }

         public boolean isWeapon(ItemStack stack) {
            return stack.getItem() instanceof ItemCrossbow;
         }

         public boolean isAmmo(ItemStack stack) {
            return stack.getItem() instanceof ItemBolt;
         }
      });
      info.add(new QuiverHelper.IQuiverInfo() {
         public boolean isQuiver(ItemStack stack) {
            return OreDictionarySW.matches("spartanweaponry:arrow_quivers", stack);
         }

         public boolean isWeapon(ItemStack stack) {
            return stack.getItem() instanceof ItemBow;
         }

         public boolean isAmmo(ItemStack stack) {
            return stack.getItem() instanceof ItemArrow;
         }
      });
   }

   public interface IQuiverInfo {
      boolean isQuiver(ItemStack var1);

      boolean isWeapon(ItemStack var1);

      boolean isAmmo(ItemStack var1);
   }
}
