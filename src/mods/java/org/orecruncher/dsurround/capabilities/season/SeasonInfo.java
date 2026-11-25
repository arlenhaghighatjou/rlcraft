package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.dsurround.registry.biome.BiomeUtil;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.chunk.ClientChunkCache;

@SideOnly(Side.CLIENT)
public class SeasonInfo implements ISeasonInfo {
   protected static final float FREEZE_TEMP = 0.15F;
   protected static final float BREATH_TEMP = 0.2F;
   protected static final String noSeason = Localization.loadString("dsurround.season.noseason");
   protected final World world;

   public SeasonInfo() {
      this.world = null;
   }

   public SeasonInfo(@Nonnull World world) {
      this.world = world;
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   @Nonnull
   public SeasonType getSeasonType() {
      return SeasonType.NONE;
   }

   @Nonnull
   public SeasonType.SubType getSeasonSubType() {
      return SeasonType.SubType.NONE;
   }

   @Nonnull
   public String getSeasonString() {
      SeasonType season = this.getSeasonType();
      if (season == SeasonType.NONE) {
         return noSeason;
      } else {
         SeasonType.SubType sub = this.getSeasonSubType();
         String seasonStr = Localization.loadString("dsurround.season." + season.getValue());
         String subSeasonStr = Localization.loadString("dsurround.season." + sub.getValue());
         return Localization.format("dsurround.season.format", new Object[]{subSeasonStr, seasonStr});
      }
   }

   @Nonnull
   public TemperatureRating getPlayerTemperature() {
      return this.getBiomeTemperature(EnvironStateHandler.EnvironState.getPlayerPosition());
   }

   @Nonnull
   public TemperatureRating getBiomeTemperature(@Nonnull BlockPos pos) {
      return TemperatureRating.fromTemp(this.getTemperature(pos));
   }

   @Nonnull
   public BlockPos getPrecipitationHeight(@Nonnull BlockPos pos) {
      return ClientChunkCache.instance().getPrecipitationHeight(pos);
   }

   public float getFloatTemperature(@Nonnull Biome biome, @Nonnull BlockPos pos) {
      return BiomeUtil.getBiomeData(biome).getFloatTemperature(pos);
   }

   public float getTemperature(@Nonnull BlockPos pos) {
      Biome biome = ClientChunkCache.instance().getBiome(pos);
      return this.getFloatTemperature(biome, pos);
   }

   public boolean canWaterFreeze(@Nonnull BlockPos pos) {
      return this.getTemperature(pos) < 0.15F;
   }

   public boolean showFrostBreath(@Nonnull BlockPos pos) {
      return this.getTemperature(pos) < 0.2F;
   }

   protected boolean doDust(@Nonnull BiomeInfo biome) {
      return ModOptions.fog.allowDesertFog && Weather.notDoVanilla() && biome.getHasDust();
   }

   @Nonnull
   public PrecipitationType getPrecipitationType(@Nonnull BlockPos pos, @Nullable BiomeInfo biome) {
      if (biome == null) {
         biome = BiomeUtil.getBiomeData(ClientChunkCache.instance().getBiome(pos));
      }

      if (!biome.hasWeatherEffect()) {
         return PrecipitationType.NONE;
      } else if (this.doDust(biome)) {
         return PrecipitationType.DUST;
      } else {
         return this.getFloatTemperature(biome.getBiome(), pos) < 0.15F ? PrecipitationType.SNOW : PrecipitationType.RAIN;
      }
   }

   @Nonnull
   public static SeasonInfo factory(@Nonnull World world) {
      String dimName = world.provider.getDimensionType().getName();
      if (world.provider.getDimension() == -1) {
         ModBase.log().debug("Creating Nether SeasonInfo for dimension %s", new Object[]{dimName});
         return new SeasonInfoNether(world);
      } else {
         if (ModEnvironment.SereneSeasons.isLoaded()) {
            if (SeasonInfoSereneSeasons.isWorldWhitelisted(world)) {
               ModBase.log().debug("Creating Serene Seasons SeasonInfo for dimension %s", new Object[]{dimName});
               return new SeasonInfoSereneSeasons(world);
            }

            ModBase.log().debug("Serene Seasons is installed but the dimension %s is not whitelisted", new Object[]{dimName});
         }

         ModBase.log().debug("Creating default SeasonInfo for dimension %s", new Object[]{dimName});
         return new SeasonInfo(world);
      }
   }
}
