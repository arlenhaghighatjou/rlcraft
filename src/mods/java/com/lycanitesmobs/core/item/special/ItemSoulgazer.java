package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemSoulgazer extends ItemBase {
   public ItemSoulgazer() {
      this.setMaxStackSize(1);
      this.itemName = "soulgazer";
      this.setup();
      this.setContainerItem(this);
   }

   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
      super.onUpdate(itemStack, world, entity, par4, par5);
   }

   public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (playerExt == null) {
         return false;
      } else {
         int amount = CreatureManager.getInstance().config.creatureStudyKnowledge;
         if (!playerExt.studyCreature(entity, amount, true, true)) {
            return false;
         } else {
            if (player.getEntityWorld().isRemote) {
               for(int i = 0; i < 32; ++i) {
                  entity.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (double)((float)entity.getPosition().getX() + 4.0F * player.getRNG().nextFloat() - 2.0F), (double)((float)entity.getPosition().getY() + 4.0F * player.getRNG().nextFloat() - 2.0F), (double)((float)entity.getPosition().getZ() + 4.0F * player.getRNG().nextFloat() - 2.0F), 0.0D, 0.0D, 0.0D, new int[0]);
               }
            }

            return true;
         }
      }
   }

   public boolean showDurabilityBar(ItemStack itemStack) {
      if (LycanitesMobs.proxy.getClientPlayer() != null) {
         ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.proxy.getClientPlayer());
         if (extendedPlayer != null) {
            return extendedPlayer.creatureStudyCooldown > 0;
         }
      }

      return false;
   }

   public double getDurabilityForDisplay(ItemStack itemStack) {
      if (LycanitesMobs.proxy.getClientPlayer() != null) {
         ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.proxy.getClientPlayer());
         if (extendedPlayer != null) {
            return (double)extendedPlayer.creatureStudyCooldown / (double)extendedPlayer.creatureStudyCooldownMax;
         }
      }

      return 0.0D;
   }
}
