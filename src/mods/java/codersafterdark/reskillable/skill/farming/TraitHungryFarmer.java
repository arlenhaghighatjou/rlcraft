package codersafterdark.reskillable.skill.farming;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.base.LevelLockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TraitHungryFarmer extends Trait {
   public TraitHungryFarmer() {
      super(new ResourceLocation("Reskillable", "hungry_farmer"), 2, 3, new ResourceLocation("reskillable", "farming"), 8, "reskillable:farming|32");
   }

   public void onPlayerTick(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      if (player != null && !player.isCreative() && !player.isSpectator()) {
         PlayerData data = PlayerDataHandler.get(player);
         if (data != null && data.getSkillInfo(this.getParentSkill()).isUnlocked(this)) {
            int playerHunger = player.getFoodStats().getFoodLevel();
            if (playerHunger < 20) {
               NonNullList<ItemStack> inventoryList = player.inventoryContainer.getInventory();
               ItemStack currentStack = ItemStack.EMPTY;
               int hungerNeeded = 20 - playerHunger;
               int bestHungerPoints = 0;

               for(ItemStack stack : inventoryList) {
                  if (!stack.isEmpty() && stack.getItem() instanceof ItemFood && LevelLockHandler.canPlayerUseItem(player, stack)) {
                     int hungerPoints = ((ItemFood)stack.getItem()).getHealAmount(stack);
                     if (currentStack.isEmpty() || hungerPoints < bestHungerPoints && hungerPoints >= hungerNeeded || hungerPoints > bestHungerPoints && bestHungerPoints < hungerNeeded) {
                        currentStack = stack;
                        bestHungerPoints = hungerPoints;
                        if (hungerPoints == hungerNeeded) {
                           break;
                        }
                     }
                  }
               }

               if (!currentStack.isEmpty()) {
                  currentStack.getItem().onItemUseFinish(currentStack, player.getEntityWorld(), player);
               }
            }
         }

      }
   }
}
