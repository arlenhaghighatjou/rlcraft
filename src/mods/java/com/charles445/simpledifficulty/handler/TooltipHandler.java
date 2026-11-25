package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import java.text.DecimalFormat;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipHandler {
   private final DecimalFormat df = new DecimalFormat("#.##");

   public TooltipHandler() {
   }

   @SubscribeEvent
   public void onItemTooltipEvent(ItemTooltipEvent event) {
      if (TemperatureUtil.getArmorTemperatureTag(event.getItemStack()) != 0.0F) {
         float tempTag = TemperatureUtil.getArmorTemperatureTag(event.getItemStack());
         if (tempTag > 0.0F) {
            event.getToolTip().add(TextFormatting.DARK_RED + " Temperature +" + this.df.format((double)tempTag));
         } else {
            event.getToolTip().add(TextFormatting.DARK_BLUE + " Temperature " + this.df.format((double)tempTag));
         }
      }

   }
}
