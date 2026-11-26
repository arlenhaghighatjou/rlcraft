package com.dhanantry.scapeandrunparasites.util.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.IGrowable;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SRPEventHandlerTerrainGen {
   public SRPEventHandlerTerrainGen() {
   }

   public void register(EventBus bus) {
      bus.register(SaplingGrowTreeEvent.class, this::allowTreeGrowthTick);
   }

   public void allowTreeGrowthTick(SaplingGrowTreeEvent event) {
      if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof IGrowable) {
         if (event.getWorld().getBlockState(event.getPos()).getBlock() != SRPBlocks.ParasiteSapling) {
            int heart = ParasiteEventWorld.canBiomeStillExist(event.getWorld(), event.getPos(), false);
            switch(heart) {
            case 1:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeOne) {
                  event.setResult(Result.DENY);
               }
               break;
            case 2:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeTwo) {
                  event.setResult(Result.DENY);
               }
               break;
            case 3:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeThree) {
                  event.setResult(Result.DENY);
               }
            }

         }
      }
   }
}
