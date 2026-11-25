package xzeroair.trinkets.races.elf;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.elf.config.ElfConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceElfAttributes extends RaceAttributesWrapper {
   public static final ElfConfig serverConfig;

   public RaceElfAttributes() {
      this.size = 100;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 16374701;
      this.color2 = 11107684;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.elf;
   }
}
