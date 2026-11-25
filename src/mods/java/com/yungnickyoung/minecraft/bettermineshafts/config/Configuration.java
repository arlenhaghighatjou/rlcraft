package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(
   modid = "bettermineshafts",
   name = "bettermineshafts-1_12_2"
)
public class Configuration {
   @Name("Mineshaft Spawn Rate")
   @RequiresWorldRestart
   @Comment({"Default: .003"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public static double mineshaftSpawnRate = 0.003D;
   @Name("Spawn Rates & More")
   @Comment({"Customize the spawn rates for various mineshaft parts and decorations."})
   public static SpawnRatesConfig spawnRates = new SpawnRatesConfig();
   @Name("Ore Deposits")
   @Comment({"Configure ore deposit spawn chances. MAKE SURE ALL THE VALUES ADD UP TO 100, or things won't work the way you want them to!!"})
   public static OresConfig ores = new OresConfig();
   @Name("Mod Compatibility")
   public static ModCompatConfig modCompat = new ModCompatConfig();

   public Configuration() {
   }
}
