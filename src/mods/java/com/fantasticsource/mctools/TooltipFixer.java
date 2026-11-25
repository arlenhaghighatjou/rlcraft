package com.fantasticsource.mctools;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipFixer {
   public TooltipFixer() {
   }

   @SubscribeEvent
   public static void configTooltip(Pre event) {
      List<String> lines = event.getLines();
      boolean fixNewlines = false;
      boolean fillScreen = false;

      for(String string : lines) {
         if (string.contains("FIXNEWLINES")) {
            fixNewlines = true;
         }

         if (string.contains("FILLSCREEN")) {
            fillScreen = true;
         }
      }

      if (fixNewlines || fillScreen) {
         String[] oldLines = (String[])lines.toArray(new String[lines.size()]);
         List<String> var13 = new ArrayList();
         if (!fixNewlines) {
            for(String string : oldLines) {
               var13.add(string.replaceAll("FILLSCREEN[ ]*", ""));
            }
         } else {
            for(String string : oldLines) {
               for(String s : string.split("\\\\n")) {
                  var13.add(s.replaceAll("FIXNEWLINES[ ]*", "").replaceAll("FILLSCREEN[ ]*", ""));
               }
            }
         }

         event.setCanceled(true);
         if (fillScreen) {
            GuiUtils.drawHoveringText(var13, 0, 0, event.getScreenWidth(), event.getScreenHeight(), Integer.MAX_VALUE, event.getFontRenderer());
         } else {
            GuiUtils.drawHoveringText(var13, event.getX(), event.getY(), event.getScreenWidth(), event.getScreenHeight(), event.getMaxWidth(), event.getFontRenderer());
         }
      }

   }
}
