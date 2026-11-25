package xzeroair.trinkets.races.dragon;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.dragon.config.DragonConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceDragonAttributes extends RaceAttributesWrapper {
   public static final DragonConfig serverConfig;

   public RaceDragonAttributes() {
      this.size = 120;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 3289650;
      this.color2 = 9509561;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.dragon;
   }
}
