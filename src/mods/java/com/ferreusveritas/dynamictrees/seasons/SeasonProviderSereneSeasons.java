package com.ferreusveritas.dynamictrees.seasons;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import sereneseasons.api.season.Season.SubSeason;
import sereneseasons.config.BiomeConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonASMHelper;

public class SeasonProviderSereneSeasons implements ISeasonProvider {
   private float seasonValue = 1.0F;

   public SeasonProviderSereneSeasons() {
   }

   public Float getSeasonValue(World world, BlockPos pos) {
      return this.seasonValue;
   }

   public void updateTick(World world, long worldTicks) {
      this.seasonValue = ((float)sereneseasons.api.season.SeasonHelper.getSeasonState(world).getSubSeason().ordinal() + 0.5F) / (float)SubSeason.VALUES.length * 4.0F;
   }

   public boolean shouldSnowMelt(World world, BlockPos pos) {
      if (ModConfig.seasons.generateSnowAndIce && this.seasonValue < 3.0F) {
         Biome biome = world.getBiome(pos);
         return BiomeConfig.enablesSeasonalEffects(biome) && SeasonASMHelper.getFloatTemperature(world, biome, pos) >= 0.15F;
      } else {
         return false;
      }
   }
}
