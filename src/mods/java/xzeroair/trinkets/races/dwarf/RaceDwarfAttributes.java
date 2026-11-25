package xzeroair.trinkets.races.dwarf;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.dwarf.config.DwarfConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceDwarfAttributes extends RaceAttributesWrapper {
   public static final DwarfConfig serverConfig;

   public RaceDwarfAttributes() {
      this.size = 75;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 10832170;
      this.color2 = 7039851;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.dwarf;
   }
}
