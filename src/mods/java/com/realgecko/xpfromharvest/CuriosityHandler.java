package com.realgecko.xpfromharvest;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class CuriosityHandler {
   public CuriosityHandler() {
   }

   public void register(EventBus bus) {
      bus.register(RightClickBlock.class, EventPriority.HIGHEST, false, this::handleRightClick);
   }

   public void handleRightClick(RightClickBlock event) {
      if (event.getWorld() instanceof WorldServer) {
         if (event.getEntityPlayer().isSneaking()) {
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            event.getEntityPlayer().sendMessage(new TextComponentString(state.toString()));
         }

      }
   }
}
