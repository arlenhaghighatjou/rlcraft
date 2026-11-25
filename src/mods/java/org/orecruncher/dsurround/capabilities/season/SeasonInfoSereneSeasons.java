package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.chunk.ClientChunkCache;
import sereneseasons.api.season.BiomeHooks;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.api.season.Season.TropicalSeason;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;

@SideOnly(Side.CLIENT)
public class SeasonInfoSereneSeasons extends SeasonInfo {
   public SeasonInfoSereneSeasons(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   public SeasonType getSeasonType() {
      if (this.world == null) {
         return SeasonType.SPRING;
      } else {
         Season season = SeasonHelper.getSeasonState(this.world).getSeason();
         switch(season) {
         case SUMMER:
            return SeasonType.SUMMER;
         case AUTUMN:
            return SeasonType.AUTUMN;
         case WINTER:
            return SeasonType.WINTER;
         case SPRING:
            return SeasonType.SPRING;
         default:
            return SeasonType.NONE;
         }
      }
   }

   @Nonnull
   public SeasonType.SubType getSeasonSubType() {
      if (this.world == null) {
         return SeasonType.SubType.MID;
      } else {
         SubSeason sub = SeasonHelper.getSeasonState(this.world).getSubSeason();
         switch(sub) {
         case EARLY_SUMMER:
         case EARLY_AUTUMN:
         case EARLY_WINTER:
         case EARLY_SPRING:
            return SeasonType.SubType.EARLY;
         case MID_SUMMER:
         case MID_AUTUMN:
         case MID_WINTER:
         case MID_SPRING:
            return SeasonType.SubType.MID;
         case LATE_SUMMER:
         case LATE_AUTUMN:
         case LATE_WINTER:
         case LATE_SPRING:
            return SeasonType.SubType.LATE;
         default:
            return SeasonType.SubType.NONE;
         }
      }
   }

   public float getFloatTemperature(@Nonnull Biome biome, @Nonnull BlockPos pos) {
      return BiomeHooks.getFloatTemperature(this.world, biome, pos);
   }

   @Nonnull
   public PrecipitationType getPrecipitationType(@Nonnull BlockPos pos, @Nullable BiomeInfo biome) {
      if (this.world == null) {
         return PrecipitationType.NONE;
      } else {
         if (biome == null) {
            biome = BiomeUtil.getBiomeData(ClientChunkCache.instance().getBiome(pos));
         }

         Biome trueBiome = biome.getBiome();
         if (trueBiome != null && BiomeConfig.usesTropicalSeasons(trueBiome)) {
            TropicalSeason tropicalSeason = SeasonHelper.getSeasonState(this.world).getTropicalSeason();
            switch(tropicalSeason) {
            case MID_DRY:
               return PrecipitationType.NONE;
            case MID_WET:
               return PrecipitationType.RAIN;
            }
         }

         return super.getPrecipitationType(pos, biome);
      }
   }

   public static boolean isWorldWhitelisted(@Nonnull World world) {
      return SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension());
   }
}
