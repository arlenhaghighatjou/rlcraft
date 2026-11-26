package mcjty.lostcities;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import mcjty.lostcities.config.LostCityProfile;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;

public class ClientEventHandlers {
   public ClientEventHandlers() {
   }

   public void register(EventBus bus) {
      bus.register(FogColors.class, this::onFogEvent);
      bus.register(FogDensity.class, this::onFogDensity);
   }

   public void onFogEvent(FogColors event) {
      if (WorldTypeTools.isLostCities(event.getEntity().world)) {
         LostCityProfile profile = WorldTypeTools.getProfile(event.getEntity().world);
         if (profile.FOG_RED >= 0.0F) {
            event.setRed(profile.FOG_RED);
         }

         if (profile.FOG_GREEN >= 0.0F) {
            event.setGreen(profile.FOG_GREEN);
         }

         if (profile.FOG_BLUE >= 0.0F) {
            event.setBlue(profile.FOG_BLUE);
         }
      }

   }

   public void onFogDensity(FogDensity event) {
      if (WorldTypeTools.isLostCities(event.getEntity().world)) {
         LostCityProfile profile = WorldTypeTools.getProfile(event.getEntity().world);
         if (profile.FOG_DENSITY >= 0.0F) {
            event.setDensity(profile.FOG_DENSITY);
            event.setCanceled(true);
         }
      }

   }
}
