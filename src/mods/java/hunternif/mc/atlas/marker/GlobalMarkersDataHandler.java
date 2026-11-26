package hunternif.mc.atlas.marker;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

public class GlobalMarkersDataHandler {
   private static final String DATA_KEY = "aAtlasGlobalMarkers";
   private GlobalMarkersData data;

   public GlobalMarkersDataHandler() {
   }

   public void register(EventBus bus) {
      bus.register(Load.class, EventPriority.HIGHEST, false, this::onWorldLoad);
      bus.register(ClientConnectedToServerEvent.class, this::onClientConnectedToServer);
      bus.register(PlayerLoggedInEvent.class, this::onPlayerLogin);
   }

   public void onWorldLoad(Load event) {
      if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
         this.data = (GlobalMarkersData)event.getWorld().loadData(GlobalMarkersData.class, "aAtlasGlobalMarkers");
         if (this.data == null) {
            this.data = new GlobalMarkersData("aAtlasGlobalMarkers");
            this.data.markDirty();
            event.getWorld().setData("aAtlasGlobalMarkers", this.data);
         }
      }

   }

   public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
      if (!event.isLocal()) {
         this.data = null;
      }

   }

   public GlobalMarkersData getData() {
      if (this.data == null) {
         this.data = new GlobalMarkersData("aAtlasGlobalMarkers");
      }

      return this.data;
   }

   public void onPlayerLogin(PlayerLoggedInEvent event) {
      this.data.syncOnPlayer(event.player);
   }
}
