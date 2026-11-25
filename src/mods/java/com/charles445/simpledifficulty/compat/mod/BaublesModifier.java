package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureIdentity;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BaublesModifier extends ModifierBase {
   private boolean enabled;
   private Class c_BaublesApi;
   private Method m_BaublesApi_getBaubles;

   public BaublesModifier() {
      super("Baubles");

      try {
         this.c_BaublesApi = Class.forName("baubles.api.BaublesApi");
         this.m_BaublesApi_getBaubles = this.c_BaublesApi.getMethod("getBaubles", EntityPlayer.class);
         this.enabled = true;
      } catch (Exception var2) {
         SimpleDifficulty.logger.error("BaublesModifier reflection failed! Baubles compatibility is now disabled!", var2);
         this.enabled = false;
      }

   }

   public float getPlayerInfluence(EntityPlayer player) {
      if (this.enabled && ModConfig.server.compatibility.toggles.baubles) {
         try {
            IInventory inventory = (IInventory)this.m_BaublesApi_getBaubles.invoke((Object)null, player);
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
         } catch (Exception var6) {
            SimpleDifficulty.logger.error("BaublesModifier reflection failed during getPlayerInfluence! Baubles compatibility is now disabled!", var6);
            this.enabled = false;
         }
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
