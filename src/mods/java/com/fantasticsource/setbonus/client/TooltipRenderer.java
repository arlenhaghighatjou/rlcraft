package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.Tools;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipRenderer {
   public TooltipRenderer() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void tooltips(ItemTooltipEvent event) {
      if (SetBonusConfig.clientSettings.enableTooltips) {
         EntityPlayer player = event.getEntityPlayer();
         if (player != null) {
            List<String> tooltip = event.getToolTip();
            boolean edited = false;

            for(Set set : ClientData.sets.values()) {
               for(ItemFilter filter : set.involvedEquips.values()) {
                  if (filter.matches(event.getItemStack())) {
                     if (!edited) {
                        edited = true;
                        tooltip.add("");
                     }

                     int count = set.getNumberEquipped(player);
                     int max = set.getMaxNumber();
                     String color = "" + (count == 0 ? TextFormatting.RED : (count == max ? TextFormatting.GREEN : TextFormatting.YELLOW));
                     tooltip.add(color + TextFormatting.BOLD + "=== " + set.name + " (" + count + "/" + max + ") ===");

                     for(ClientBonus bonus : ClientData.bonuses.values()) {
                        int req = 0;
                        boolean otherReqs = false;

                        for(ABonusRequirement requirement : bonus.bonusRequirements) {
                           if (requirement instanceof SetRequirement) {
                              SetRequirement setRequirement = (SetRequirement)requirement;
                              if (setRequirement.set.id.equals(set.id)) {
                                 req = Tools.max(new int[]{req, setRequirement.num});
                              } else {
                                 otherReqs = true;
                              }
                           } else {
                              otherReqs = true;
                           }
                        }

                        if (req > 0) {
                           ClientBonus.BonusInstance bonusInstance = bonus.getBonusInstance(player);
                           color = "";
                           int active = set.getNumberEquipped(player);
                           if (bonusInstance.active) {
                              color = color + TextFormatting.GREEN;
                           } else if (active >= req) {
                              color = color + TextFormatting.DARK_PURPLE;
                           } else if (active == 0) {
                              color = color + TextFormatting.RED;
                           } else {
                              color = color + TextFormatting.YELLOW;
                           }

                           tooltip.add(color + " (" + active + "/" + req + ")" + (otherReqs ? "*" : "") + " " + bonus.name);
                        }
                     }

                     tooltip.add("");
                  }
               }
            }

         }
      }
   }
}
