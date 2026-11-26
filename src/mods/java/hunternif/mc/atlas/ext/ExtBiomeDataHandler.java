package hunternif.mc.atlas.ext;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class ExtBiomeDataHandler {
   private static final String DATA_KEY = "aAtlasExtTiles";
   private ExtBiomeData data;

   public ExtBiomeDataHandler() {
   }

   public void register(EventBus bus) {
      bus.register(Load.class, EventPriority.HIGHEST, false, this::onWorldLoad);
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLogin);
   }

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

   public void onPlayerLogin(PlayerLoggedInEvent event) {
      ExtTileIdMap.instance().syncOnPlayer(event.player);
      this.data.syncOnPlayer(event.player);
   }
}
