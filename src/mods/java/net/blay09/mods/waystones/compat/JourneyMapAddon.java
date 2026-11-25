package net.blay09.mods.waystones.compat;

import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.util.WaystoneActivatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ClientPlugin
public class JourneyMapAddon implements IClientPlugin {
   private IClientAPI api;

   public JourneyMapAddon() {
   }

   public void initialize(IClientAPI iClientAPI) {
      this.api = iClientAPI;
      MinecraftForge.EVENT_BUS.register(this);
   }

   public String getModId() {
      return "waystones";
   }

   public void onEvent(ClientEvent clientEvent) {
   }

   @SubscribeEvent
   public void onWaystoneActivated(WaystoneActivatedEvent event) {
      if (WaystoneConfig.compat.createJourneyMapWaypoint) {
         if (!event.getWaystoneName().isEmpty()) {
            try {
               this.api.show(new Waypoint("waystones", event.getWaystoneName(), event.getDimension(), event.getPos()));
            } catch (Exception var3) {
               var3.printStackTrace();
            }

         }
      }
   }
}
