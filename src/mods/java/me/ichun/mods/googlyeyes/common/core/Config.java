package me.ichun.mods.googlyeyes.common.core;

import java.io.File;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;

public class Config extends ConfigBase {
   @ConfigProp(
      changeable = false
   )
   public String[] disabledGoogly = new String[0];
   @ConfigProp
   @IntBool
   public int acidTripEyes = 0;
   @ConfigProp
   @IntMinMax(
      min = 0,
      max = 100
   )
   public int googlyEyeChance = 20;
   @ConfigProp
   @IntBool
   public int allPlayersHaveGooglyEyes = 0;

   public Config(File file) {
      super(file);
   }

   public String getModId() {
      return "googlyeyes";
   }

   public String getModName() {
      return "Googly Eyes";
   }
}
