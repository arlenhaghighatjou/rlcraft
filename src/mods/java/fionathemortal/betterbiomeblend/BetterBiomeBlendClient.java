package fionathemortal.betterbiomeblend;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber({Side.CLIENT})
public final class BetterBiomeBlendClient {
   public static final int BIOME_BLEND_RADIUS_MAX = 14;
   public static final int BIOME_BLEND_RADIUS_MIN = 0;

   public BetterBiomeBlendClient() {
   }

   @SubscribeEvent
   public static void onChunkLoadedEvent(Load event) {
      Chunk chunk = event.getChunk();
      World world = event.getWorld();
      ColorChunkCache cache = BiomeColor.getColorChunkCacheForWorld(world);
      if (cache != null) {
         cache.invalidateNeighbourhood(chunk.x, chunk.z);
      }

   }

   @SubscribeEvent
   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("betterbiomeblend")) {
         ConfigManager.sync("betterbiomeblend", Type.INSTANCE);
      }

   }
}
