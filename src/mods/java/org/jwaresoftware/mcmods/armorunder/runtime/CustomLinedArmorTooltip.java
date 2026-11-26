package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;

@SideOnly(Side.CLIENT)
public final class CustomLinedArmorTooltip {
   public CustomLinedArmorTooltip() {
   }

   public void register(EventBus bus) {
      bus.register(ItemTooltipEvent.class, this::addSpecialLinedArmorTooltip);
   }

   public void addSpecialLinedArmorTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      Def defn = Armory.getXLining(stack);
      if (defn.name == XLining.OBSIDIAN_SHIELD) {
         String tip = Strings.translateFormatted("tooltip.auw.xlining.ollie", new Object[0]);
         event.getToolTip().add(TextFormatting.YELLOW + tip);
      }

      if (defn.name == XLining.ANTIFREEZE_SHIELD) {
         String tip = Strings.translateFormatted("tooltip.auw.xlining.otto", new Object[0]);
         event.getToolTip().add(TextFormatting.YELLOW + tip);
      }

   }
}
