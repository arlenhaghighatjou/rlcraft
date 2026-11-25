package sereneseasons.util;

import com.arlenh7.eaglercraft.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.Season;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

public class SeasonColourUtil {
   public SeasonColourUtil() {
   }

   public static int multiplyColours(int colour1, int colour2) {
      return (int)((float)colour1 / 255.0F * ((float)colour2 / 255.0F) * 255.0F);
   }

   public static int overlayBlendChannel(int underColour, int overColour) {
      int retVal;
      if (underColour < 128) {
         retVal = multiplyColours(2 * underColour, overColour);
      } else {
         retVal = multiplyColours(2 * (255 - underColour), 255 - overColour);
         retVal = 255 - retVal;
      }

      return retVal;
   }

   public static int overlayBlend(int underColour, int overColour) {
      int r = overlayBlendChannel(underColour >> 16 & 255, overColour >> 16 & 255);
      int g = overlayBlendChannel(underColour >> 8 & 255, overColour >> 8 & 255);
      int b = overlayBlendChannel(underColour & 255, overColour & 255);
      return (r & 255) << 16 | (g & 255) << 8 | b & 255;
   }

   public static int saturateColour(int colour, float saturationMultiplier) {
      float[] hsb = Color.RGBtoHSB(colour >> 16 & 255, colour >> 8 & 255, colour & 255, (float[])null);
      hsb[1] *= saturationMultiplier;
      return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 16777215;
   }

   public static int applySeasonalGrassColouring(ISeasonColorProvider colorProvider, Biome biome, int originalColour) {
      if (BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(Minecraft.getMinecraft().player.dimension)) {
         int overlay = colorProvider.getGrassOverlay();
         float saturationMultiplier = colorProvider.getGrassSaturationMultiplier();
         if (!ModConfig.seasons.changeGrassColour) {
            overlay = Season.SubSeason.MID_SUMMER.getGrassOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getGrassSaturationMultiplier();
         }

         int newColour = overlay == 16777215 ? originalColour : overlayBlend(originalColour, overlay);
         return saturationMultiplier != -1.0F ? saturateColour(newColour, saturationMultiplier) : newColour;
      } else {
         return originalColour;
      }
   }

   public static int applySeasonalFoliageColouring(ISeasonColorProvider colorProvider, Biome biome, int originalColour) {
      if (BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(Minecraft.getMinecraft().player.dimension)) {
         int overlay = colorProvider.getFoliageOverlay();
         float saturationMultiplier = colorProvider.getFoliageSaturationMultiplier();
         if (!ModConfig.seasons.changeFoliageColour) {
            overlay = Season.SubSeason.MID_SUMMER.getFoliageOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getFoliageSaturationMultiplier();
         }

         int newColour = overlay == 16777215 ? originalColour : overlayBlend(originalColour, overlay);
         return saturationMultiplier != -1.0F ? saturateColour(newColour, saturationMultiplier) : newColour;
      } else {
         return originalColour;
      }
   }
}
