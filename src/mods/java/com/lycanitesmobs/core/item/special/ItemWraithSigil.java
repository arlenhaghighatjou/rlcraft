package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.creature.EntityWraith;
import com.lycanitesmobs.core.item.temp.ItemScepter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemWraithSigil extends ItemScepter {
   public ItemWraithSigil() {
      this.modInfo = LycanitesMobs.modInfo;
      this.itemName = "wraithsigil";
      this.setup();
   }

   public int getDurability() {
      return 250;
   }

   public int getRapidTime(ItemStack itemStack) {
      return 8;
   }

   public boolean rapidAttack(ItemStack itemStack, World world, EntityLivingBase entity) {
      if (!world.isRemote) {
         for(int i = -2; i <= 2; ++i) {
            EntityWraith minion = new EntityWraith(world);
            minion.setPositionAndRotation(entity.posX, entity.posY + 1.0D, entity.posZ, entity.rotationYaw + (float)(5 * i), -entity.rotationPitch);
            minion.setMinion(true);
            if (entity instanceof EntityPlayer) {
               minion.setPlayerOwner((EntityPlayer)entity);
               minion.ownerUUID = entity.getUniqueID();
            }

            world.spawnEntity(minion);
            minion.playAttackSound();
            minion.chargeAttack();
         }
      }

      return true;
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return repairStack.getItem() == Items.GUNPOWDER ? true : super.getIsRepairable(itemStack, repairStack);
   }
}
