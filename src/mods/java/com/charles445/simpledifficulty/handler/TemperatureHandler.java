package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TemperatureHandler {
   public TemperatureHandler() {
   }

   @SubscribeEvent
   public void onLivingEntityUseItemFinish(Finish event) {
      if (QuickConfig.isTemperatureEnabled()) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (player.world.isRemote) {
               return;
            }

            ItemStack stack = event.getItem();
            List<JsonConsumableTemperature> consumableList = (List)JsonConfig.consumableTemperature.get(stack.getItem().getRegistryName().toString());
            if (consumableList != null) {
               for(JsonConsumableTemperature jct : consumableList) {
                  if (jct != null && jct.matches(stack)) {
                     SDCapabilities.getTemperatureData(player).setTemporaryModifier(jct.group, jct.temperature, jct.duration);
                     break;
                  }
               }
            }
         }

      }
   }
}
