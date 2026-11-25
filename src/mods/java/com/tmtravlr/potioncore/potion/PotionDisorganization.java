package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class PotionDisorganization extends PotionCorePotion {
   public static final String NAME = "disorganization";
   public static final PotionDisorganization INSTANCE = new PotionDisorganization();

   public PotionDisorganization() {
      super("disorganization", true, 10027263);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         int inventoryHalf = player.inventory.mainInventory.size() / 2;

         for(int i = 0; i < inventoryHalf; ++i) {
            int slot = player.getRNG().nextInt(inventoryHalf);
            ItemStack swapStack = (ItemStack)player.inventory.mainInventory.get(i);
            player.inventory.mainInventory.set(i, player.inventory.mainInventory.get(inventoryHalf + slot));
            player.inventory.mainInventory.set(inventoryHalf + slot, swapStack);
         }

         int slot = player.getRNG().nextInt(player.inventory.mainInventory.size());
         ItemStack swapStack = (ItemStack)player.inventory.mainInventory.get(slot);
         player.inventory.mainInventory.set(slot, player.getHeldItemOffhand());
         player.setHeldItem(EnumHand.OFF_HAND, swapStack);
         player.inventory.markDirty();
      }

   }
}
