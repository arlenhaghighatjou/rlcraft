package c4.comforts.client;

import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerClient {
   public EventHandlerClient() {
   }

   @SubscribeEvent
   public void onPlayerRender(Pre evt) {
      EntityPlayer player = evt.getEntityPlayer();
      if (player instanceof EntityOtherPlayerMP && player.isPlayerSleeping() && player.bedLocation != null) {
         Block bed = player.world.getBlockState(player.bedLocation).getBlock();
         if (bed instanceof BlockSleepingBag) {
            player.renderOffsetY = -0.375F;
         } else if (bed instanceof BlockHammock) {
            player.renderOffsetY = -0.5F;
         }
      }

   }
}
