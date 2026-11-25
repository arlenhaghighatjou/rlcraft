package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class BiomeFogRangeCalculator extends VanillaFogRangeCalculator {
   protected static final int DISTANCE = 20;
   protected static final float DUST_FOG_IMPACT = 0.9F;
   protected final BiomeFogRangeCalculator.Context[] context = new BiomeFogRangeCalculator.Context[]{new BiomeFogRangeCalculator.Context(), new BiomeFogRangeCalculator.Context()};

   public BiomeFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "BiomeFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      EntityLivingBase player = EnvironStateHandler.EnvironState.getPlayer();
      IBlockAccessEx provider = ClientChunkCache.instance();
      int playerX = MathStuff.floor(player.posX);
      int playerZ = MathStuff.floor(player.posZ);
      float rainStr = Weather.getIntensityLevel();
      BiomeFogRangeCalculator.Context ctx = this.context[event.getFogMode() == -1 ? 0 : 1];
      if (ctx.returnCached(playerX, playerZ, rainStr, event)) {
         return ctx.cached;
      } else {
         MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
         float fpDistanceBiomeFog = 0.0F;
         float weightBiomeFog = 0.0F;
         boolean isRaining = Weather.isRaining();
         ctx.rain = rainStr;
         ctx.doScan = false;

         for(int x = -20; x <= 20; ++x) {
            for(int z = -20; z <= 20; ++z) {
               pos.setPos(playerX + x, 0, playerZ + z);
               ctx.doScan |= !provider.isAvailable(pos);
               BiomeInfo biome = BiomeUtil.getBiomeData(provider.getBiome(pos));
               float distancePart = 1.0F;
               float weightPart = 1.0F;
               if (isRaining && biome.getHasDust()) {
                  distancePart = 1.0F - 0.9F * rainStr;
               } else if (biome.getHasFog()) {
                  distancePart = biome.getFogDensity();
               }

               fpDistanceBiomeFog += distancePart;
               ++weightBiomeFog;
            }
         }

         float weightMixed = 1681.0F;
         float weightDefault = 1681.0F - weightBiomeFog;
         float fpDistanceBiomeFogAvg = weightBiomeFog == 0.0F ? 0.0F : fpDistanceBiomeFog / weightBiomeFog;
         float rangeConst = Math.max(240.0F, event.getFarPlaneDistance() - 16.0F);
         float farPlaneDistance = (fpDistanceBiomeFog * rangeConst + event.getFarPlaneDistance() * weightDefault) / 1681.0F;
         float farPlaneDistanceScaleBiome = 0.1F * (1.0F - fpDistanceBiomeFogAvg) + 0.75F * fpDistanceBiomeFogAvg;
         float farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75F * weightDefault) / 1681.0F;
         ctx.posX = playerX;
         ctx.posZ = playerZ;
         ctx.lastFarPlane = event.getFarPlaneDistance();
         farPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
         ctx.cached.set(event.getFogMode(), farPlaneDistance, farPlaneDistanceScale);
         return ctx.cached;
      }
   }

   private static class Context {
      public int posX;
      public int posZ;
      public float rain;
      public float lastFarPlane;
      public boolean doScan;
      public final FogResult cached;

      private Context() {
         this.doScan = true;
         this.cached = new FogResult();
      }

      public boolean returnCached(int pX, int pZ, float r, @Nonnull RenderFogEvent event) {
         return !this.doScan && pX == this.posX && pZ == this.posZ && r == this.rain && this.lastFarPlane == event.getFarPlaneDistance() && this.cached.isValid(event);
      }
   }
}
