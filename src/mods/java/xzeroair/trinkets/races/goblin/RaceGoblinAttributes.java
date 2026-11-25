package xzeroair.trinkets.races.goblin;

import xzeroair.trinkets.races.RaceAttributesWrapper;
import xzeroair.trinkets.races.goblin.config.GoblinConfig;
import xzeroair.trinkets.util.TrinketsConfig;

public class RaceGoblinAttributes extends RaceAttributesWrapper {
   public static final GoblinConfig serverConfig;

   public RaceGoblinAttributes() {
      this.size = 50;
      this.width = this.size;
      this.height = this.size;
      this.color1 = 6588004;
      this.color2 = 3096367;
      this.color3 = this.color1;
      this.opacity = 1.0F;
      this.trait_opacity = 1.0F;
      this.attributes = serverConfig.attributes;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.goblin;
   }
}
