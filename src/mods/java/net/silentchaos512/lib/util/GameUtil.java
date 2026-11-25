package net.silentchaos512.lib.util;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public final class GameUtil {
   private GameUtil() {
      throw new IllegalAccessError("Utility class");
   }

   public static boolean isClient() {
      return FMLCommonHandler.instance().getSide().isClient();
   }

   public static boolean isServer() {
      return FMLCommonHandler.instance().getSide().isServer();
   }

   public static boolean isDeobfuscated() {
      return ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
   }

   public static boolean shouldCalculateTooltip() {
      LoaderState state = Loader.instance().getLoaderState();
      return state != LoaderState.INITIALIZATION && state != LoaderState.SERVER_ABOUT_TO_START && state != LoaderState.SERVER_STOPPING;
   }
}
