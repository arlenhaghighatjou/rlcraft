package org.orecruncher.dsurround.client.renderer;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.lib.ItemStackUtil;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dsurround"
)
public class ToolTipProvider {
   public ToolTipProvider() {
   }

   @SubscribeEvent
   public static void tooltipEvent(@Nonnull ItemTooltipEvent event) {
      if (ModOptions.logging.enableDebugLogging) {
         ItemStack stack = event.getItemStack();
         String itemName = ItemStackUtil.getItemName(stack);
         if (itemName != null) {
            event.getToolTip().add(TextFormatting.GOLD + itemName);
            event.getToolTip().add(TextFormatting.GOLD + stack.getItem().getClass().getName());
         }
      }

   }
}
