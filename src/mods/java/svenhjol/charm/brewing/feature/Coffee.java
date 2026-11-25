package svenhjol.charm.brewing.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.brewing.potion.CoffeePotion;
import svenhjol.meson.Feature;

public class Coffee extends Feature {
   public static CoffeePotion coffee;
   public static int duration;

   public Coffee() {
   }

   public String getDescription() {
      return "Brew cocoa beans in water to make Coffee which gives you a helpful boost.";
   }

   public void configure() {
      super.configure();
      duration = this.propInt("Coffee effect duration", "Duration (in seconds) of Coffee effects.", 10);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      coffee = new CoffeePotion();
   }
}
