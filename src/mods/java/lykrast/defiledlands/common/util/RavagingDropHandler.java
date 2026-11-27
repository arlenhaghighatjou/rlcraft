package lykrast.defiledlands.common.util;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class RavagingDropHandler {
   public RavagingDropHandler() {
   }

   public static void register(EventBus bus) {
      bus.register(HarvestDropsEvent.class, EventPriority.LOWEST, false, RavagingDropHandler::OnBlockBreak);
   }

   public static void OnBlockBreak(HarvestDropsEvent e) {
      if (!e.getWorld().isRemote && e.getHarvester() != null) {
         Item item = e.getHarvester().getHeldItemMainhand().getItem();
         if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("ravaging")) {
            e.getDrops().clear();
         }
      }

   }
}
