package lykrast.defiledlands.common.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class RavagingDropHandler {
   public RavagingDropHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void OnBlockBreak(HarvestDropsEvent e) {
      if (!e.getWorld().isRemote && e.getHarvester() != null) {
         Item item = e.getHarvester().getHeldItemMainhand().getItem();
         if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("ravaging")) {
            e.getDrops().clear();
         }
      }

   }
}
