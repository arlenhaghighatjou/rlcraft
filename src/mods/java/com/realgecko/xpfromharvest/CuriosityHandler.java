package com.realgecko.xpfromharvest;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CuriosityHandler {
   public CuriosityHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void handleRightClick(RightClickBlock event) {
      if (event.getWorld() instanceof WorldServer) {
         if (event.getEntityPlayer().isSneaking()) {
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            event.getEntityPlayer().sendMessage(new TextComponentString(state.toString()));
         }

      }
   }
}
