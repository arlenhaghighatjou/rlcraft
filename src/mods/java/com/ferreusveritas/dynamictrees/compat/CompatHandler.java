package com.ferreusveritas.dynamictrees.compat;

import com.ferreusveritas.dynamictrees.seasons.SeasonGrowthCalculatorActive;
import com.ferreusveritas.dynamictrees.seasons.SeasonGrowthCalculatorNull;
import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import com.ferreusveritas.dynamictrees.seasons.SeasonManager;
import com.ferreusveritas.dynamictrees.seasons.SeasonProviderNull;
import com.ferreusveritas.dynamictrees.seasons.SeasonProviderSereneSeasons;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;

public class CompatHandler {
   public CompatHandler() {
   }

   public static void preInit() {
      if (Loader.isModLoaded("sereneseasons")) {
         HandleSereneSeasons();
      }

   }

   @Method(
      modid = "sereneseasons"
   )
   public static void HandleSereneSeasons() {
      SeasonManager seasonManager = new SeasonManager((world) -> {
         return SeasonsConfig.isDimensionWhitelisted(world.provider.getDimension()) ? new Tuple(new SeasonProviderSereneSeasons(), new SeasonGrowthCalculatorActive()) : new Tuple(new SeasonProviderNull(), new SeasonGrowthCalculatorNull());
      });
      seasonManager.setTropicalPredicate((world, pos) -> {
         return BiomeConfig.usesTropicalSeasons(world.getBiome(pos));
      });
      SeasonHelper.setSeasonManager(seasonManager);
   }
}
