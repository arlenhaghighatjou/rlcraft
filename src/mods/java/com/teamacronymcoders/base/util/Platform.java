package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.IBaseMod;
import javax.annotation.Nullable;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;

public class Platform {
   public Platform() {
   }

   public static boolean isDevEnv() {
      return ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
   }

   @Nullable
   public static IBaseMod getCurrentMod() {
      Object activeMod = Loader.instance().activeModContainer().getMod();
      if (activeMod instanceof IBaseMod) {
         return (IBaseMod)activeMod;
      } else {
         FMLLog.bigWarning("Mods using BASE must have their mod class extend IBaseMod!", new Object[]{""});
         return null;
      }
   }

   public static void attemptLogErrorToCurrentMod(String logString) {
      IBaseMod mod = getCurrentMod();
      if (mod != null) {
         mod.getLogger().error(logString);
      }

   }

   public static void attemptLogExceptionToCurrentMod(Throwable throwable) {
      IBaseMod mod = getCurrentMod();
      if (mod != null) {
         mod.getLogger().getLogger().error(throwable);
      }

   }
}
