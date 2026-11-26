package com.ferreusveritas.dynamictrees.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class LeafUpdateEventHandler {
   public LeafUpdateEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(NeighborNotifyEvent.class, EventPriority.HIGH, false, this::UpdateNeighbour);
   }

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
