package net.silentchaos512.lib.util;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
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
      return FMLLaunchHandler.isDeobfuscatedEnvironment();
   }

   public static boolean shouldCalculateTooltip() {
      LoaderState state = Loader.instance().getLoaderState();
      return state != LoaderState.INITIALIZATION && state != LoaderState.SERVER_ABOUT_TO_START && state != LoaderState.SERVER_STOPPING;
   }
}
