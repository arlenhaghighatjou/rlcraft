package fionathemortal.betterbiomeblend;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.relauncher.Side;

public final class BetterBiomeBlendClient {
   public static final int BIOME_BLEND_RADIUS_MAX = 14;
   public static final int BIOME_BLEND_RADIUS_MIN = 0;

   public BetterBiomeBlendClient() {
   }

   public static void register(EventBus bus) {
      bus.register(Load.class, BetterBiomeBlendClient::onChunkLoadedEvent);
      bus.register(OnConfigChangedEvent.class, BetterBiomeBlendClient::onConfigChanged);
   }

   public static void onChunkLoadedEvent(Load event) {
      Chunk chunk = event.getChunk();
      World world = event.getWorld();
      ColorChunkCache cache = BiomeColor.getColorChunkCacheForWorld(world);
      if (cache != null) {
         cache.invalidateNeighbourhood(chunk.x, chunk.z);
      }

   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("betterbiomeblend")) {
         ConfigManager.sync("betterbiomeblend", Type.INSTANCE);
      }

   }
}
