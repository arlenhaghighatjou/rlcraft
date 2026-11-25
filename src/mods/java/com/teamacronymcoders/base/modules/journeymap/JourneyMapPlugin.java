package com.teamacronymcoders.base.modules.journeymap;

import com.teamacronymcoders.base.Base;
import javax.annotation.Nonnull;
import journeymap.client.api.ClientPlugin;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.minecraftforge.common.MinecraftForge;

@ClientPlugin
public class JourneyMapPlugin implements IClientPlugin {
   public JourneyMapPlugin() {
   }

   public void initialize(@Nonnull IClientAPI jAPI) {
      if (Base.instance.getModuleHandler().isModuleEnabled("JourneyMap")) {
         MinecraftForge.EVENT_BUS.register(new JourneyMapWaypointListener(jAPI));
      }

   }

   public String getModId() {
      return "base";
   }

   public void onEvent(@Nonnull ClientEvent event) {
   }
}
