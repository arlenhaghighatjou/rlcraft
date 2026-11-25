package xzeroair.trinkets.races.titan;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.titan.config.TitanConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceTitanAttributes extends RaceAttributesWrapper {
   public static final TitanConfig serverConfig;

   public RaceTitanAttributes() {
      this.size = 300;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 10066329;
      this.color2 = 3223595;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.titan;
   }
}
