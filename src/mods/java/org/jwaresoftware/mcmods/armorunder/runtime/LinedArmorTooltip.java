package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.armorunder.recipes.AutoregLinedArmorRecipe;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;

@SideOnly(Side.CLIENT)
public final class LinedArmorTooltip {
   public LinedArmorTooltip() {
   }

   public void register(EventBus bus) {
      bus.register(ItemTooltipEvent.class, this::addPlainLinedArmorTooltip);
      bus.register(ItemTooltipEvent.class, this::addAutoregLiningTooltip);
   }

   public void addPlainLinedArmorTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      Def defn = Armory.getXLining(stack);
      if (defn.name == XLining.TEMPERATURE_REGULATOR) {
         String what = defn.value == 0 ? "MILD" : (defn.value < 0 ? "COOL" : "WARM");
         String tip = Strings.translateFormatted("tooltip.auw.xlining.ozzy", new Object[]{what});
         event.getToolTip().add(TextFormatting.YELLOW + tip);
      } else {
         String tip = Armory.getTLiningTooltip(stack);
         if (tip != null) {
            event.getToolTip().add(tip);
         }
      }

   }

   public void addAutoregLiningTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (!stack.isEmpty() && AutoregLinedArmorRecipe.isAutoregLining(stack.getItem())) {
         String tip = AutoregLinedArmorRecipe.getLiningModifierTooltip(stack);
         if (tip != null) {
            event.getToolTip().add(TextFormatting.GOLD + tip);
         }
      }

   }
}
