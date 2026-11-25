package sereneseasons.season;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

public class SeasonASMHelper {
   public SeasonASMHelper() {
   }

   public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState) {
      return canSnowAtInSeason(world, pos, checkLight, seasonState, false);
   }

   public static boolean canSnowAtInSeason(World world, BlockPos pos, boolean checkLight, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature) {
      Biome biome = world.getBiome(pos);
      float temperature = biome.getTemperature(pos);
      if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         if (BiomeConfig.usesTropicalSeasons(biome)) {
            return false;
         }

         temperature = getFloatTemperature(world, biome, pos);
      }

      if (temperature >= 0.15F) {
         return false;
      } else if (biome.getDefaultTemperature() >= 0.15F && !ModConfig.seasons.generateSnowAndIce) {
         return false;
      } else if (checkLight) {
         if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock().isAir(state, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, @Nullable ISeasonState seasonState) {
      return canBlockFreezeInSeason(world, pos, noWaterAdj, seasonState, false);
   }

   public static boolean canBlockFreezeInSeason(World world, BlockPos pos, boolean noWaterAdj, @Nullable ISeasonState seasonState, boolean useUnmodifiedTemperature) {
      Biome biome = world.getBiome(pos);
      float temperature = biome.getTemperature(pos);
      if (BiomeConfig.enablesSeasonalEffects(biome) && !useUnmodifiedTemperature && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         if (BiomeConfig.usesTropicalSeasons(biome)) {
            return false;
         }

         temperature = getFloatTemperature(world, biome, pos);
      }

      if (temperature >= 0.15F) {
         return false;
      } else if (biome.getDefaultTemperature() >= 0.15F && !ModConfig.seasons.generateSnowAndIce) {
         return false;
      } else {
         if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            IBlockState iblockstate = world.getBlockState(pos);
            Block block = iblockstate.getBlock();
            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0) {
               if (!noWaterAdj) {
                  return true;
               }

               boolean flag = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
               if (!flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isRainingAtInSeason(World world, BlockPos pos, ISeasonState seasonState) {
      Biome biome = world.getBiome(pos);
      if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         Season.TropicalSeason tropicalSeason = seasonState.getTropicalSeason();
         switch(tropicalSeason) {
         case MID_DRY:
            return false;
         case MID_WET:
            return true;
         default:
            return biome.canRain();
         }
      } else {
         return !biome.getEnableSnow() && !world.canSnowAt(pos, false) ? biome.canRain() : false;
      }
   }

   public static float getFloatTemperature(World world, Biome biome, BlockPos pos) {
      return !SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()) ? biome.getTemperature(pos) : getFloatTemperature((new SeasonTime(SeasonHelper.getSeasonState(world).getSeasonCycleTicks())).getSubSeason(), biome, pos);
   }

   public static float getFloatTemperature(Season.SubSeason subSeason, Biome biome, BlockPos pos) {
      boolean tropicalBiome = BiomeConfig.usesTropicalSeasons(biome);
      float biomeTemp = biome.getTemperature(pos);
      if (!tropicalBiome && biome.getDefaultTemperature() <= 0.8F && BiomeConfig.enablesSeasonalEffects(biome)) {
         switch(subSeason) {
         case LATE_SPRING:
         case EARLY_AUTUMN:
            biomeTemp = MathHelper.clamp(biomeTemp - 0.1F, -0.5F, 2.0F);
            break;
         case MID_SPRING:
         case MID_AUTUMN:
            biomeTemp = MathHelper.clamp(biomeTemp - 0.2F, -0.5F, 2.0F);
            break;
         case EARLY_SPRING:
         case LATE_AUTUMN:
            biomeTemp = MathHelper.clamp(biomeTemp - 0.4F, -0.5F, 2.0F);
            break;
         case EARLY_WINTER:
         case MID_WINTER:
         case LATE_WINTER:
            biomeTemp = MathHelper.clamp(biomeTemp - 0.8F, -0.5F, 2.0F);
         }
      }

      return biomeTemp;
   }

   public static boolean shouldRenderRainSnow(World world, Biome biome) {
      if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();
         switch(tropicalSeason) {
         case MID_DRY:
            return false;
         case MID_WET:
            return true;
         default:
            return biome.canRain() || biome.getEnableSnow();
         }
      } else {
         return biome.canRain() || biome.getEnableSnow();
      }
   }

   public static boolean shouldAddRainParticles(World world, Biome biome) {
      if (BiomeConfig.usesTropicalSeasons(biome) && BiomeConfig.enablesSeasonalEffects(biome) && SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension())) {
         Season.TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(world).getTropicalSeason();
         switch(tropicalSeason) {
         case MID_DRY:
            return false;
         case MID_WET:
            return true;
         default:
            return biome.canRain();
         }
      } else {
         return biome.canRain();
      }
   }
}
