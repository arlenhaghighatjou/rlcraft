package org.orecruncher.lib.chunk;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.LibBase;

@SideOnly(Side.CLIENT)
public final class ClientChunkCache {
   private static IBlockAccessEx INSTANCE;
   private static int range = 34;

   public ClientChunkCache() {
   }

   public static IBlockAccessEx instance() {
      if (INSTANCE == null) {
         LibBase.log().warn("Initializing ClientChunkCache on the fly!");
         INSTANCE = new PassThroughChunkCache();
         MinecraftForge.EVENT_BUS.register(ClientChunkCache.class);
      }

      return INSTANCE;
   }

   public static void initialize(int blockRange, boolean useDirect) {
      INSTANCE = (IBlockAccessEx)(useDirect ? new DirectChunkCache() : new PassThroughChunkCache());
      range = blockRange;
      MinecraftForge.EVENT_BUS.register(ClientChunkCache.class);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void clientTick(@Nonnull ClientTickEvent event) {
      if (INSTANCE != null) {
         if (event.side == Side.CLIENT && event.phase == Phase.START) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null) {
               BlockPos pos = new BlockPos(player.getPosition());
               BlockPos min = pos.add(-range, -range, -range);
               BlockPos max = pos.add(range, range, range);
               ((IChunkCache)INSTANCE).update(player.getEntityWorld(), min, max);
            } else {
               ((IChunkCache)INSTANCE).clear();
            }
         }

      }
   }
}
