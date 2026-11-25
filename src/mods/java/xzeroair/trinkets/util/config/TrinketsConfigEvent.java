package xzeroair.trinkets.util.config;

import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.util.TrinketsConfig;

public class TrinketsConfigEvent {
   public static TrinketsConfigEvent instance = new TrinketsConfigEvent();

   public TrinketsConfigEvent() {
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("xat")) {
         TrinketsConfig.Save();
         ConfigHelper.TrinketConfigStorage.init();
      }

   }
}
