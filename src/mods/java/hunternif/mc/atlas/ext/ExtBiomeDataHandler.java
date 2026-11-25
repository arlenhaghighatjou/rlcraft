package hunternif.mc.atlas.ext;

import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ExtBiomeDataHandler {
   private static final String DATA_KEY = "aAtlasExtTiles";
   private ExtBiomeData data;

   public ExtBiomeDataHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onWorldLoad(Load event) {
      if (!event.getWorld().isRemote) {
         this.data = (ExtBiomeData)event.getWorld().loadData(ExtBiomeData.class, "aAtlasExtTiles");
         if (this.data == null) {
            this.data = new ExtBiomeData("aAtlasExtTiles");
            this.data.markDirty();
            event.getWorld().setData("aAtlasExtTiles", this.data);
         }
      }

   }

   public ExtBiomeData getData() {
      if (this.data == null) {
         this.data = new ExtBiomeData("aAtlasExtTiles");
      }

      return this.data;
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      ExtTileIdMap.instance().syncOnPlayer(event.player);
      this.data.syncOnPlayer(event.player);
   }
}
