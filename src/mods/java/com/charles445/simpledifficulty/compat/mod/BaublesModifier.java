package com.charles445.simpledifficulty.compat.mod;

import baubles.api.BaublesApi;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BaublesModifier extends ModifierBase {
   public BaublesModifier() {
      super("Baubles");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      if (ModConfig.server.compatibility.toggles.baubles) {
         IInventory inventory = BaublesApi.getBaubles(player);
         if (inventory == null) {
            return 0.0F;
         }

         float sum = 0.0F;

         for(int i = 0; i < inventory.getSizeInventory(); ++i) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
               sum = sum + this.processStackJSON(stack);
               sum = sum + TemperatureUtil.getArmorTemperatureTag(stack);
            }
         }

         return sum;
      }

      return 0.0F;
   }

   private float processStackJSON(ItemStack stack) {
      List<JsonTemperatureIdentity> armorList = (List)JsonConfig.armorTemperatures.get(stack.getItem().getRegistryName().toString());
      if (armorList != null) {
         for(JsonTemperatureIdentity jtm : armorList) {
            if (jtm != null && jtm.matches(stack)) {
               return jtm.temperature;
            }
         }
      }

      return 0.0F;
   }
}
