package xzeroair.trinkets.races.faelis;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.faelis.config.FaelisConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceFaelisAttributes extends RaceAttributesWrapper {
   public static final FaelisConfig serverConfig;

   public RaceFaelisAttributes() {
      this.size = 85;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 16571252;
      this.color2 = 4465933;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.faelis;
   }
}
