package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModifierHeldItems extends ModifierBase {
   public ModifierHeldItems() {
      super("HeldItems");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      ItemStack mainHand = player.getHeldItemMainhand();
      ItemStack offHand = player.getHeldItemOffhand();
      float sum = 0.0F;
      if (!mainHand.isEmpty()) {
         sum += this.processStack(mainHand);
      }

      if (!offHand.isEmpty()) {
         sum += this.processStack(offHand);
      }

      return sum;
   }

   private float processStack(ItemStack stack) {
      List<JsonTemperatureIdentity> heldItemList = (List)JsonConfig.heldItemTemperatures.get(stack.getItem().getRegistryName().toString());
      if (heldItemList != null) {
         for(JsonTemperatureIdentity jtm : heldItemList) {
            if (jtm != null && jtm.matches(stack)) {
               return jtm.temperature;
            }
         }
      }

      return 0.0F;
   }
}
