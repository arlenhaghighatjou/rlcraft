package com.charles445.simpledifficulty.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import java.text.DecimalFormat;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class TooltipHandler {
   private final DecimalFormat df = new DecimalFormat("#.##");

   public TooltipHandler() {
   }

   public void register(EventBus bus) {
      bus.register(ItemTooltipEvent.class, this::onItemTooltipEvent);
   }

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
