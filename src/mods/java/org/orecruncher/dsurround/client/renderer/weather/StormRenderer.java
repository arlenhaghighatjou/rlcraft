package org.orecruncher.dsurround.client.renderer.weather;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.CapabilityDimensionInfo;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.PrecipitationType;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.client.weather.compat.RandomThings;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class StormRenderer {
   private static final double[] RAIN_X_COORDS = new double[1024];
   private static final double[] RAIN_Y_COORDS = new double[1024];
   private final EaglercraftRandom random = new XorShiftRandom();
   private final MutableBlockPos mutable = new MutableBlockPos();
   private static ResourceLocation effectTexture;
   private static boolean isDrawing;
   private static WorldRenderer worldrenderer;
   private static int locY;

   public StormRenderer() {
   }

   private static void setupForRender(@Nonnull ResourceLocation r) {
      if (effectTexture != r) {
         if (worldrenderer == null) {
            worldrenderer = Tessellator.getInstance().getBuffer();
            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            worldrenderer.setTranslation(-manager.viewerPosX, -manager.viewerPosY, -manager.viewerPosZ);
            locY = MathHelper.floor(manager.viewerPosY);
         }

         if (isDrawing) {
            Tessellator.getInstance().draw();
         }

         effectTexture = r;
         Minecraft.getMinecraft().getTextureManager().bindTexture(r);
         worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
         isDrawing = true;
      }

   }

   private static void closeRender() {
      if (isDrawing) {
         Tessellator.getInstance().draw();
      }

      if (worldrenderer != null) {
         worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
      }

      isDrawing = false;
      effectTexture = null;
      worldrenderer = null;
   }

   public void render(@Nonnull EntityRenderer renderer, float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      Entity entity = mc.getRenderViewEntity();
      if (entity != null) {
         World world = mc.world;
         IDimensionInfo info = CapabilityDimensionInfo.getCapability(world);
         if (info == null || info.hasWeather()) {
            float rainStrength = Weather.getIntensityLevel();
            if (rainStrength > 0.0F) {
               float alphaRatio = rainStrength / Weather.getMaxIntensityLevel();
               renderer.enableLightmap();
               GlStateManager.disableCull();
               GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
               GlStateManager.alphaFunc(516, 0.1F);
               int range = mc.gameSettings.fancyGraphics ? 10 : 5;
               int renderCount = RenderWeather.getRendererUpdateCount();
               float f1 = (float)renderCount + partialTicks;
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);
               Weather.Properties props = Weather.getWeatherProperties();
               BlockPos playerPos = EnvironStateHandler.EnvironState.getPlayerPosition();
               int playerX = playerPos.getX();
               int playerY = playerPos.getY();
               int playerZ = playerPos.getZ();

               for(int gridZ = playerZ - range; gridZ <= playerZ + range; ++gridZ) {
                  for(int gridX = playerX - range; gridX <= playerX + range; ++gridX) {
                     int idx = (gridZ - playerZ + 16) * 32 + gridX - playerX + 16;
                     double rainX = RAIN_X_COORDS[idx];
                     double rainY = RAIN_Y_COORDS[idx];
                     this.mutable.setPos(gridX, 0, gridZ);
                     if (!RandomThings.shouldRain(world, this.mutable)) {
                        BiomeInfo biome = BiomeUtil.getBiomeData(ClientChunkCache.instance().getBiome(this.mutable));
                        int precipHeight = season.getPrecipitationHeight(this.mutable).getY();
                        int k2 = Math.max(playerY - range, precipHeight);
                        int l2 = Math.max(playerY + range, precipHeight);
                        if (k2 != l2) {
                           this.mutable.setPos(gridX, k2, gridZ);
                           PrecipitationType pt = season.getPrecipitationType(this.mutable, biome);
                           if (pt != PrecipitationType.NONE) {
                              int seed = gridZ << 16 ^ gridX;
                              this.random.setSeed((long)seed);
                              int i3 = Math.max(precipHeight, locY);
                              double d6 = (double)((float)gridX + 0.5F) - entity.posX;
                              double d7 = (double)((float)gridZ + 0.5F) - entity.posZ;
                              float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float)range;
                              this.mutable.setPos(gridX, i3, gridZ);
                              int combinedLight = ClientChunkCache.instance().getCombinedLight(this.mutable, 0);
                              if (pt == PrecipitationType.RAIN) {
                                 setupForRender(props.getRainTexture());
                                 double d5 = ((double)(renderCount + seed & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                                 float alpha = ((1.0F - f3 * f3) * 0.5F + 0.5F) * alphaRatio;
                                 int slX16 = combinedLight >> 16 & '\uffff';
                                 int blX16 = combinedLight & '\uffff';
                                 worldrenderer.pos((double)gridX - rainX + 0.5D, (double)k2, (double)gridZ - rainY + 0.5D).tex(0.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX + rainX + 0.5D, (double)k2, (double)gridZ + rainY + 0.5D).tex(1.0D, (double)k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX + rainX + 0.5D, (double)l2, (double)gridZ + rainY + 0.5D).tex(1.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX - rainX + 0.5D, (double)l2, (double)gridZ - rainY + 0.5D).tex(0.0D, (double)l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, alpha).lightmap(slX16, blX16).endVertex();
                              } else {
                                 ResourceLocation texture;
                                 Color color;
                                 if (pt == PrecipitationType.DUST) {
                                    color = biome.getDustColor();
                                    texture = props.getDustTexture();
                                 } else {
                                    color = Color.WHITE;
                                    texture = props.getSnowTexture();
                                 }

                                 setupForRender(texture);
                                 double d8 = (double)(((float)(renderCount & 511) + partialTicks) / 512.0F);
                                 float factor = biome.getHasDust() ? 0.2F : 0.01F;
                                 double d9 = this.random.nextDouble() + (double)f1 * (double)factor * (double)((float)this.random.nextGaussian());
                                 double d10 = this.random.nextDouble() + (double)(f1 * (float)this.random.nextGaussian()) * 0.001D;
                                 float alpha = ((1.0F - f3 * f3) * 0.3F + 0.5F) * alphaRatio;
                                 int cl = (combinedLight * 3 + 15728880) / 4;
                                 int slX16 = cl >> 16 & '\uffff';
                                 int blX16 = cl & '\uffff';
                                 worldrenderer.pos((double)gridX - rainX + 0.5D, (double)k2, (double)gridZ - rainY + 0.5D).tex(0.0D + d9, (double)k2 * 0.25D + d8 + d10).color(color.red, color.green, color.blue, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX + rainX + 0.5D, (double)k2, (double)gridZ + rainY + 0.5D).tex(1.0D + d9, (double)k2 * 0.25D + d8 + d10).color(color.red, color.green, color.blue, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX + rainX + 0.5D, (double)l2, (double)gridZ + rainY + 0.5D).tex(1.0D + d9, (double)l2 * 0.25D + d8 + d10).color(color.red, color.green, color.blue, alpha).lightmap(slX16, blX16).endVertex();
                                 worldrenderer.pos((double)gridX - rainX + 0.5D, (double)l2, (double)gridZ - rainY + 0.5D).tex(0.0D + d9, (double)l2 * 0.25D + d8 + d10).color(color.red, color.green, color.blue, alpha).lightmap(slX16, blX16).endVertex();
                              }
                           }
                        }
                     }
                  }
               }

               closeRender();
               GlStateManager.enableCull();
               GlStateManager.disableBlend();
               GlStateManager.alphaFunc(516, 0.1F);
               renderer.disableLightmap();
            }
         }
      }
   }

   static {
      for(int i = 0; i < 32; ++i) {
         for(int j = 0; j < 32; ++j) {
            double f2 = (double)(j - 16);
            double f3 = (double)(i - 16);
            double f4 = (double)MathHelper.sqrt(f2 * f2 + f3 * f3);
            RAIN_X_COORDS[i << 5 | j] = -f3 / f4 * 0.5D;
            RAIN_Y_COORDS[i << 5 | j] = f2 / f4 * 0.5D;
         }
      }

      effectTexture = null;
      isDrawing = false;
      worldrenderer = null;
   }
}
