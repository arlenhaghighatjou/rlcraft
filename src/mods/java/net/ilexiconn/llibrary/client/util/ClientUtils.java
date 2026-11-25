package net.ilexiconn.llibrary.client.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientUtils {
   private static long lastUpdate = System.currentTimeMillis();

   public ClientUtils() {
   }

   public static void updateLast() {
      lastUpdate = System.currentTimeMillis();
   }

   public static float updateValue(float current, float target) {
      return updateValue(current, target, 0.5F);
   }

   public static float updateValue(float current, float target, float factor) {
      float times = (float)(System.currentTimeMillis() - lastUpdate) / 16.666666F;
      float off;
      off = (off = target - current) <= 0.01F && off >= -0.01F ? 0.0F : off * (float)Math.pow((double)factor, (double)times);
      return target - off;
   }

   public static float interpolate(float prev, float current, float partialTicks) {
      return prev + partialTicks * (current - prev);
   }

   public static float interpolateRotation(float prev, float current, float partialTicks) {
      float shortest = ((current - prev) % 360.0F + 540.0F) % 360.0F - 180.0F;
      return prev + shortest * partialTicks;
   }

   public static void setPlayerTexture(AbstractClientPlayer player, Type type, ResourceLocation texture) {
      if (player.hasPlayerInfo() && texture != null) {
         player.getPlayerInfo().playerTextures.put(type, texture);
      }

   }

   public static void setPlayerSkinType(AbstractClientPlayer player, String type) {
      player.getPlayerInfo().skinType = type;
   }

   public static void addPlayerSkinTypeRenderer(String type, RenderPlayer renderPlayer) {
      ClientProxy.MINECRAFT.getRenderManager().getSkinMap().put(type, renderPlayer);
   }
}
