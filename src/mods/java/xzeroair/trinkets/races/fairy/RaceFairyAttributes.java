package xzeroair.trinkets.races.fairy;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.fairy.config.FairyConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceFairyAttributes extends RaceAttributesWrapper {
   public static final FairyConfig serverConfig;

   public RaceFairyAttributes() {
      this.size = 25;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 12514535;
      this.color2 = 962222;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.fairy;
   }
}
