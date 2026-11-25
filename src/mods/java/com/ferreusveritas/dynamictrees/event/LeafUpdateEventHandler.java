package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LeafUpdateEventHandler {
   public LeafUpdateEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void UpdateNeighbour(NeighborNotifyEvent event) {
      World world = event.getWorld();

      for(EnumFacing facing : event.getNotifiedSides()) {
         BlockPos blockPos = event.getPos().offset(facing);
         if (TreeHelper.isLeaves(world.getBlockState(blockPos))) {
            event.setCanceled(true);
         }
      }

   }
}
