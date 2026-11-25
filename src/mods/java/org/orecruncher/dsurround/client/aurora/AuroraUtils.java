package org.orecruncher.dsurround.client.aurora;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.TimeUtils;

@SideOnly(Side.CLIENT)
public class AuroraUtils {
   public static final int PLAYER_FIXED_Y_OFFSET = 64;
   public static final int PLAYER_FIXED_Z_OFFSET = 150;
   public static final int AURORA_PEAK_AGE = 512;
   public static final int AURORA_AGE_RATE = 1;

   private AuroraUtils() {
   }

   public static int getChunkRenderDistance() {
      return Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
   }

   public static float getTimeSeconds() {
      return ((float)EnvironStateHandler.EnvironState.getTickCounter() + EnvironStateHandler.EnvironState.getPartialTick()) / 20.0F;
   }

   public static boolean dimensionHasAuroras() {
      return EnvironStateHandler.EnvironState.getDimensionInfo().hasAuroras();
   }

   public static long getSeed() {
      return TimeUtils.getGMTDaySeedBase() + (long)EnvironStateHandler.EnvironState.getClock().getDay();
   }
}
