package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class BiomeFogColorCalculator extends VanillaFogColorCalculator {
   protected int posX;
   protected int posZ;
   protected double weightBiomeFog;
   protected Color biomeFogColor;
   protected boolean doScan = true;

   public BiomeFogColorCalculator() {
   }

   @Nonnull
   public Color calculate(@Nonnull FogColors event) {
      EntityLivingBase player = EnvironStateHandler.EnvironState.getPlayer();
      World world = EnvironStateHandler.EnvironState.getWorld();
      IBlockAccessEx provider = ClientChunkCache.instance();
      int playerX = MathStuff.floor(player.posX);
      int playerZ = MathStuff.floor(player.posZ);
      GameSettings settings = Minecraft.getMinecraft().gameSettings;
      int[] ranges = ForgeModContainer.blendRanges;
      int distance = 6;
      if (settings.fancyGraphics && ranges.length > 0) {
         distance = ranges[MathStuff.clamp(settings.renderDistanceChunks, 0, ranges.length - 1)];
      }

      MutableBlockPos pos = new MutableBlockPos(0, 0, 0);
      this.doScan |= this.posX != playerX || this.posZ != playerZ;
      if (this.doScan) {
         this.doScan = false;
         this.posX = playerX;
         this.posZ = playerZ;
         this.biomeFogColor = new Color(0, 0, 0);
         this.weightBiomeFog = 0.0D;

         for(int x = -distance; x <= distance; ++x) {
            for(int z = -distance; z <= distance; ++z) {
               pos.setPos(playerX + x, 0, playerZ + z);
               this.doScan |= !provider.isAvailable(pos);
               BiomeInfo biome = BiomeUtil.getBiomeData(provider.getBiome(pos));
               Color color;
               if (biome.getHasDust()) {
                  color = biome.getDustColor();
               } else if (biome.getHasFog()) {
                  color = biome.getFogColor();
               } else {
                  color = null;
               }

               if (color != null) {
                  this.biomeFogColor.add(color);
                  ++this.weightBiomeFog;
               }
            }
         }
      }

      if (this.weightBiomeFog != 0.0D && distance != 0) {
         float partialTicks = (float)event.getRenderPartialTicks();
         float celestialAngle = world.getCelestialAngle(partialTicks);
         float baseScale = MathStuff.clamp(MathStuff.cos(celestialAngle * 3.1415927F * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
         double rScale = (double)(baseScale * 0.94F + 0.06F);
         double gScale = (double)(baseScale * 0.94F + 0.06F);
         double bScale = (double)(baseScale * 0.91F + 0.09F);
         float rainStrength = Weather.getIntensityLevel();
         if (rainStrength > 0.0F) {
            rScale *= (double)(1.0F - rainStrength * 0.5F);
            gScale *= (double)(1.0F - rainStrength * 0.5F);
            bScale *= (double)(1.0F - rainStrength * 0.4F);
         }

         float thunderStrength = Weather.getThunderStrength();
         if (thunderStrength > 0.0F) {
            rScale *= (double)(1.0F - thunderStrength * 0.5F);
            gScale *= (double)(1.0F - thunderStrength * 0.5F);
            bScale *= (double)(1.0F - thunderStrength * 0.5F);
         }

         Color fogColor = new Color(this.biomeFogColor);
         fogColor.scale((float)(rScale / this.weightBiomeFog), (float)(gScale / this.weightBiomeFog), (float)(bScale / this.weightBiomeFog));
         Color processedColor = this.applyPlayerEffects(world, player, fogColor, partialTicks);
         double weightMixed = (double)((distance * 2 + 1) * (distance * 2 + 1));
         double weightDefault = weightMixed - this.weightBiomeFog;
         Color vanillaColor = super.calculate(event);
         processedColor.scale((float)this.weightBiomeFog);
         vanillaColor.scale((float)weightDefault);
         return processedColor.add(vanillaColor).scale((float)(1.0D / weightMixed));
      } else {
         return super.calculate(event);
      }
   }

   protected Color applyPlayerEffects(@Nonnull World world, @Nonnull EntityLivingBase player, @Nonnull Color fogColor, float renderPartialTicks) {
      float darkScale = (float)((player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)renderPartialTicks) * world.provider.getVoidFogYFactor());
      PotionEffect potionEffect = player.getActivePotionEffect(MobEffects.BLINDNESS);
      if (potionEffect != null) {
         int duration = potionEffect.getDuration();
         darkScale *= duration < 20 ? 1.0F - (float)duration / 20.0F : 0.0F;
      }

      if (darkScale < 1.0F) {
         darkScale = darkScale < 0.0F ? 0.0F : darkScale * darkScale;
         fogColor.scale(darkScale);
      }

      potionEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
      if (potionEffect != null) {
         int duration = potionEffect.getDuration();
         float brightness = duration > 200 ? 1.0F : 0.7F + MathStuff.sin(((float)duration - renderPartialTicks) * 3.1415927F * 0.2F) * 0.3F;
         float scale = 1.0F / fogColor.red;
         scale = Math.min(scale, 1.0F / fogColor.green);
         scale = Math.min(scale, 1.0F / fogColor.blue);
         return fogColor.scale(1.0F - brightness + scale * brightness);
      } else {
         return fogColor;
      }
   }
}
