package bettercombat.mod.util;

import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.base.LevelLockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class ReskillableHandler {
   public ReskillableHandler() {
   }

   public static boolean shouldLockAttack(EntityPlayer player, ItemStack stack) {
      if (player != null && !stack.isEmpty() && (ConfigHandler.enforceOnCreative || !player.isCreative())) {
         if (!LevelLockHandler.canPlayerUseItem(player, stack)) {
            if (!LevelLockHandler.isFake(player)) {
               LevelLockHandler.tellPlayer(player, stack, "reskillable.misc.locked.item");
               return true;
            } else {
               return ConfigHandler.enforceFakePlayers;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
