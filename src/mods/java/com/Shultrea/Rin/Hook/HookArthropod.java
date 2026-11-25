package com.Shultrea.Rin.Hook;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class HookArthropod {
   public HookArthropod() {
   }

   public static void handleArthropod(EntityLivingBase user, Entity target, boolean offhand) {
      if (user != null) {
         ItemStack stack = offhand ? user.getHeldItemOffhand() : user.getHeldItemMainhand();
         if (!stack.isEmpty()) {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
               int j = nbttaglist.getCompoundTagAt(i).getShort("id");
               int k = nbttaglist.getCompoundTagAt(i).getShort("lvl");
               if (Enchantment.getEnchantmentByID(j) != null) {
                  Enchantment ench = Enchantment.getEnchantmentByID(j);
                  if (ench instanceof EnchantmentBase) {
                     EnchantmentBase enchant = (EnchantmentBase)ench;
                     if (enchant.isEnabled()) {
                        enchant.onEntityDamagedAlt(user, target, stack, k);
                     }
                  }
               }
            }
         }
      }

   }

   public static void hookArthropod(EntityLivingBase user, Entity target) {
      if (user != null) {
         boolean offhand = false;
         if (stackTraceHasClass("bettercombat.mod.network.PacketOffhandAttack$Handler")) {
            offhand = true;
         }

         ItemStack stack = offhand ? user.getHeldItemOffhand() : user.getHeldItemMainhand();
         if (!stack.isEmpty()) {
            NBTTagList nbttaglist = stack.getEnchantmentTagList();

            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
               int j = nbttaglist.getCompoundTagAt(i).getShort("id");
               int k = nbttaglist.getCompoundTagAt(i).getShort("lvl");
               if (Enchantment.getEnchantmentByID(j) != null) {
                  Enchantment ench = Enchantment.getEnchantmentByID(j);
                  if (ench instanceof EnchantmentBase) {
                     EnchantmentBase enchant = (EnchantmentBase)ench;
                     if (enchant.isEnabled()) {
                        enchant.onEntityDamagedAlt(user, target, stack, k);
                     }
                  }
               }
            }
         }
      }

   }

   public static boolean stackTraceHasClass(String clazz) {
      for(StackTraceElement ste : Thread.currentThread().getStackTrace()) {
         if (ste.getClassName().equals(clazz)) {
            return true;
         }
      }

      return false;
   }
}
