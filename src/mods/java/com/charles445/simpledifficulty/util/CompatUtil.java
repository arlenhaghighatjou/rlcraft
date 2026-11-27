package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.SDCompatibility;
import net.minecraftforge.fml.common.Loader;

public class CompatUtil {
   public CompatUtil() {
   }

   public static boolean canUseMod(String modid) {
      return Loader.isModLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid);
   }
}
