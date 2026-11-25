package reborncore.common.multiblock;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MultiblockEventHandler {
   public MultiblockEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public void onChunkLoad(Load loadEvent) {
      Chunk chunk = loadEvent.getChunk();
      World world = loadEvent.getWorld();
      MultiblockRegistry.onChunkLoaded(world, chunk);
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public void onWorldUnload(Unload unloadWorldEvent) {
      MultiblockRegistry.onWorldUnloaded(unloadWorldEvent.getWorld());
   }
}
