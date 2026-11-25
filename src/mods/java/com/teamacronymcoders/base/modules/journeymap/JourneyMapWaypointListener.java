package com.teamacronymcoders.base.modules.journeymap;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.event.PlaceWaypointEvent;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Waypoint;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JourneyMapWaypointListener {
   private IClientAPI jAPI;

   public JourneyMapWaypointListener(IClientAPI jAPI) {
      this.jAPI = jAPI;
   }

   @SubscribeEvent
   public void onWaypointPlaced(PlaceWaypointEvent e) {
      try {
         this.jAPI.show((new Waypoint("base", e.getName() + e.getDim() + e.getPos().hashCode(), e.getName(), e.getDim(), e.getPos())).setColor(e.getColor()));
      } catch (Throwable var3) {
         Base.instance.getLogger().warning(var3.getLocalizedMessage());
      }

   }
}
