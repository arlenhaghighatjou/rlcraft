package fionathemortal.betterbiomeblend;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(
   modid = "betterbiomeblend"
)
public class BetterBiomeBlendConfig {
   @Name("Blend Radius")
   @RangeInt(
      min = 0,
      max = 14
   )
   @RequiresWorldRestart
   public static int blendRadius = 14;

   public BetterBiomeBlendConfig() {
   }
}
