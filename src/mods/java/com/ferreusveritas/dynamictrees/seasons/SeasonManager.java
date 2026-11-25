package com.ferreusveritas.dynamictrees.seasons;

import com.ferreusveritas.dynamictrees.api.seasons.ClimateZoneType;
import com.ferreusveritas.dynamictrees.api.seasons.ISeasonGrowthCalculator;
import com.ferreusveritas.dynamictrees.api.seasons.ISeasonManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonManager implements ISeasonManager {
   private final Map<Integer, SeasonContext> seasonContextMap = new HashMap();
   private Function<World, Tuple<ISeasonProvider, ISeasonGrowthCalculator>> seasonMapper = (w) -> {
      return new Tuple(new SeasonProviderNull(), new SeasonGrowthCalculatorNull());
   };
   private static final float TROPICAL_THRESHHOLD = 0.8F;
   private BiPredicate<World, BlockPos> isTropical = (world, rootPos) -> {
      return world.getBiome(rootPos).getDefaultTemperature() > 0.8F;
   };

   public SeasonManager() {
   }

   public SeasonManager(Function<World, Tuple<ISeasonProvider, ISeasonGrowthCalculator>> seasonMapper) {
      this.seasonMapper = seasonMapper;
   }

   private Tuple<ISeasonProvider, ISeasonGrowthCalculator> createProvider(World world) {
      return (Tuple)this.seasonMapper.apply(world);
   }

   private SeasonContext getContext(World world) {
      return (SeasonContext)this.seasonContextMap.computeIfAbsent(Integer.valueOf(world.provider.getDimension()), (d) -> {
         Tuple<ISeasonProvider, ISeasonGrowthCalculator> tuple = this.createProvider(world);
         return new SeasonContext((ISeasonProvider)tuple.getFirst(), (ISeasonGrowthCalculator)tuple.getSecond());
      });
   }

   public void setProvider(World world, ISeasonProvider provider, ISeasonGrowthCalculator calc) {
      this.setProvider(world, provider, calc);
   }

   public void flushMappings() {
      this.seasonContextMap.clear();
   }

   public void setTropicalPredicate(BiPredicate<World, BlockPos> predicate) {
      this.isTropical = predicate;
   }

   public boolean isTropical(World world, BlockPos rootPos) {
      return this.isTropical.test(world, rootPos);
   }

   public void updateTick(World world, long worldTicks) {
      this.getContext(world).updateTick(world, worldTicks);
   }

   public float getGrowthFactor(World world, BlockPos rootPos, float offset) {
      SeasonContext context = this.getContext(world);
      return this.isTropical(world, rootPos) ? context.getTropicalGrowthFactor(offset) : context.getTemperateGrowthFactor(offset);
   }

   public float getSeedDropFactor(World world, BlockPos rootPos, float offset) {
      SeasonContext context = this.getContext(world);
      return this.isTropical(world, rootPos) ? context.getTropicalSeedDropFactor(offset) : context.getTemperateSeedDropFactor(offset);
   }

   public float getFruitProductionFactor(World world, BlockPos rootPos, float offset) {
      if (world == null) {
         return this.getFruitProductionFactorAsScan(world, rootPos, offset);
      } else {
         SeasonContext context = this.getContext(world);
         return this.isTropical(world, rootPos) ? context.getTropicalFruitProductionFactor(offset) : context.getTemperateFruitProductionFactor(offset);
      }
   }

   public Float getSeasonValue(World world, BlockPos pos) {
      return this.getContext(world).getSeasonProvider().getSeasonValue(world, pos);
   }

   public boolean shouldSnowMelt(World world, BlockPos pos) {
      return this.getContext(world).getSeasonProvider().shouldSnowMelt(world, pos);
   }

   public float getFruitProductionFactorAsScan(World world, BlockPos rootPos, float offset) {
      if (this.seasonContextMap.size() > 0) {
         int dim = rootPos.getX();
         float seasonValue = (float)rootPos.getY() / 64.0F;
         boolean tropical = (float)rootPos.getZ() >= 1.0F;
         if (this.seasonContextMap.containsKey(Integer.valueOf(dim))) {
            SeasonContext context = (SeasonContext)this.seasonContextMap.get(Integer.valueOf(dim));
            ISeasonGrowthCalculator calculator = context.getCalculator();
            return calculator.calcFruitProduction(Float.valueOf(seasonValue + offset), tropical ? ClimateZoneType.TROPICAL : ClimateZoneType.TEMPERATE);
         }
      }

      return 0.0F;
   }
}
