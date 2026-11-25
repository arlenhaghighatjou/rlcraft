package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class PotionRust extends PotionCorePotion {
   public static final String NAME = "rust";
   public static final PotionRust INSTANCE = new PotionRust();
   public static int damageTime = 20;

   public PotionRust() {
      super("rust", true, 7811840);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (entity.ticksExisted % damageTime == 0) {
         for(EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = entity.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.getItem().isDamageable() && !stack.getItem().getIsRepairable(stack, new ItemStack(Items.GOLD_INGOT)) && !entity.world.isRemote) {
               stack.damageItem(amplifier + 1, entity);
               if (stack.getCount() <= 0) {
                  entity.setItemStackToSlot(slot, ItemStack.EMPTY);
               }
            }
         }
      }

   }
}
