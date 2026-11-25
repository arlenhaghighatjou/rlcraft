package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class PotionRepair extends PotionCorePotion {
   public static final String NAME = "repair";
   public static final PotionRepair INSTANCE = new PotionRepair();
   public static int repairTime = 100;

   public PotionRepair() {
      super("repair", false, 7829367);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity.ticksExisted % repairTime == 0) {
         for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.getItem().isRepairable() && stack.getItem().isDamaged(stack)) {
               stack.setItemDamage(Math.max(stack.getItemDamage() - (amplifier + 1), 0));
            }
         }
      }

   }
}
