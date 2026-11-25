package com.realgecko.xpfromharvest;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockBreakHandler {
   public BlockBreakHandler() {
   }

   @SubscribeEvent
   public void handleBlockBreak(BreakEvent event) {
      if (event.getWorld() instanceof WorldServer) {
         IBlockState state = event.getWorld().getBlockState(event.getPos());
         Block block = state.getBlock();
         boolean harvest = false;
         if (Arrays.asList(ModConfig.crops).contains(state.toString())) {
            harvest = true;
         }

         if (harvest && event.getWorld().rand.nextInt(100) + 1 <= ModConfig.chance) {
            block.dropXpOnBlockBreak(event.getWorld(), event.getPos(), ModConfig.xpAmount);
         }

      }
   }
}
