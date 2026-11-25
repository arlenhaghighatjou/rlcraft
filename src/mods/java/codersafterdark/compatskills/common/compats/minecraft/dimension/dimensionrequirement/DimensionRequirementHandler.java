package codersafterdark.compatskills.common.compats.minecraft.dimension.dimensionrequirement;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import codersafterdark.reskillable.base.LevelLockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class DimensionRequirementHandler {
   public DimensionRequirementHandler() {
   }

   @SubscribeEvent
   public void onDimensionChanged(PlayerChangedDimensionEvent event) {
      EntityPlayer player = event.player;
      RequirementCache.invalidateCache(player, new Class[]{DimensionRequirement.class});

      for(int i = 0; i < player.inventory.armorInventory.size(); ++i) {
         ItemStack stack = (ItemStack)player.inventory.armorInventory.get(i);
         if (!LevelLockHandler.canPlayerUseItem(player, stack)) {
            if (!player.inventory.addItemStackToInventory(stack)) {
               player.dropItem(stack, false);
            }

            player.inventory.armorInventory.set(i, ItemStack.EMPTY);
            LevelLockHandler.tellPlayer(player, stack, "reskillable.misc.locked.armor_equip");
         }
      }

   }
}
