package rustic.client.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidClientUtil {
   public static Map<Fluid, EaglerTextureAtlasSprite> stillTextures = Maps.newHashMap();

   public FluidClientUtil() {
   }

   public static void drawGuiLiquid(FluidStack fluid, int capacity, int x, int y, int width, int height, ResourceLocation ogTexture) {
      if (fluid != null && fluid.getFluid() != null) {
         int fluidHeight = (int)((float)height * ((float)fluid.amount / (float)capacity));
         drawFluidStack(fluid, x, y + height - fluidHeight, width, fluidHeight);
         Minecraft.getMinecraft().getTextureManager().bindTexture(ogTexture);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public static void drawFluidStack(FluidStack fluid, int x, int y, int width, int height) {
      if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
         EaglerTextureAtlasSprite sprite = (EaglerTextureAtlasSprite)stillTextures.get(fluid.getFluid());
         if (sprite != null) {
            int color = fluid.getFluid().getColor();
            float minU = sprite.getMinU();
            float maxU = sprite.getMaxU();
            float minV = sprite.getMinV();
            float maxV = sprite.getMaxV();
            float uDiff = maxU - minU;
            float vDiff = maxV - minV;
            int spriteWidth = sprite.getIconWidth();
            int spriteHeight = sprite.getIconHeight();
            if (spriteWidth > 0 && spriteHeight > 0) {
               int iterationsY = height / spriteHeight;
               float leftOverY = (float)(height % spriteHeight);
               float leftOverNormY = leftOverY / (float)spriteHeight;
               int iterationsX = width / spriteWidth;
               float leftOverX = (float)(width % spriteWidth);
               float leftOverNormX = leftOverX / (float)spriteWidth;
               Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

               for(int i = 0; i < iterationsY; ++i) {
                  for(int j = 0; j < iterationsX; ++j) {
                     ClientUtils.drawTexturedColoredRect((float)(x + spriteWidth * j), (float)(y + spriteHeight * i), (float)spriteWidth, (float)spriteHeight, minU, minV, maxU, maxV, color);
                  }

                  if (leftOverX > 0.0F) {
                     ClientUtils.drawTexturedColoredRect((float)(x + spriteWidth * iterationsX), (float)(y + spriteHeight * i), leftOverX, (float)spriteHeight, minU, minV, minU + uDiff * leftOverNormX, maxV, color);
                  }
               }

               if (leftOverY > 0.0F) {
                  for(int j = 0; j < iterationsX; ++j) {
                     ClientUtils.drawTexturedColoredRect((float)(x + spriteWidth * j), (float)(y + spriteHeight * iterationsY), (float)spriteWidth, leftOverY, minU, minV, maxU, minV + vDiff * leftOverNormY, color);
                  }

                  if (leftOverX > 0.0F) {
                     ClientUtils.drawTexturedColoredRect((float)(x + spriteWidth * iterationsX), (float)(y + spriteHeight * iterationsY), leftOverX, leftOverY, minU, minV, minU + uDiff * leftOverNormX, minV + vDiff * leftOverNormY, color);
                  }
               }

            }
         }
      }
   }

   public static void initTextures(TextureMap map) {
      for(Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
         if (fluid.getStill() != null) {
            String still = fluid.getStill().toString();
            EaglerTextureAtlasSprite sprite;
            if (map.getTextureExtry(still) != null) {
               sprite = map.getTextureExtry(still);
            } else {
               sprite = map.registerSprite(fluid.getStill());
            }

            stillTextures.put(fluid, sprite);
         }
      }

   }
}
