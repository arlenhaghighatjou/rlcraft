package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.ModConfigs;
import java.util.Iterator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DropEventHandler {
   public DropEventHandler() {
   }

   @SubscribeEvent
   public void onHarvestDropsEvent(HarvestDropsEvent event) {
      if (ModConfigs.worldGen && ModConfigs.enableAppleTrees && (event.getState().getBlock() == Blocks.LEAVES || event.getState().getBlock() == Blocks.LEAVES2)) {
         Iterator<ItemStack> iter = event.getDrops().iterator();

         while(iter.hasNext()) {
            ItemStack stack = (ItemStack)iter.next();
            if (stack.getItem() == Items.APPLE) {
               iter.remove();
            }
         }
      }

   }
}
