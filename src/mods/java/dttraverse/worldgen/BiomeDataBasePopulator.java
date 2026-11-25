package dttraverse.worldgen;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.EnumChance;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.IChanceSelector;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.IDensitySelector;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.ISpeciesSelector;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.RandomSpeciesSelector;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.StaticSpeciesSelector;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase.Operation;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import prospector.traverse.world.TraverseWorld;

public final class BiomeDataBasePopulator implements IBiomeDataBasePopulator {
   public BiomeDataBasePopulator() {
   }

   public void populate(BiomeDataBase biomeDataBase) {
      Species fir = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "fir"));
      Species autumnBrown = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_brown"));
      Species autumnOrange = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_orange"));
      Species autumnRed = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_red"));
      Species autumnYellow = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "autumn_yellow"));
      Species miniJungle = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "mini_jungle"));
      Species oakShrub = TreeRegistry.findSpecies(new ResourceLocation("dttraverse", "oak_shrub"));
      Species birch = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "birch"));
      Species oak = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "oak"));
      Species spruce = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "spruce"));
      Species acacia = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "acacia"));
      Species swamp = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "oakswamp"));
      Species darkOak = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "darkoak"));
      Species cactus = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "cactus"));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.autumnalWoodsBiome, (new RandomSpeciesSelector()).add(autumnYellow, 4).add(autumnOrange, 5).add(oak, 1).add(autumnBrown, 2).add(autumnRed, 4));
      this.addDensitySelector(biomeDataBase, TraverseWorld.autumnalWoodsBiome, this.scale(0.9D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.autumnalWoodsBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.autumnalWoodedHillsBiome, (new RandomSpeciesSelector()).add(autumnYellow, 4).add(autumnOrange, 5).add(oak, 1).add(autumnBrown, 2).add(autumnRed, 4));
      this.addDensitySelector(biomeDataBase, TraverseWorld.autumnalWoodedHillsBiome, this.scale(0.9D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.autumnalWoodedHillsBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.temperateRainforestBiome, new StaticSpeciesSelector(fir));
      this.addChanceSelector(biomeDataBase, TraverseWorld.temperateRainforestBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.snowyConiferousForestBiome, new StaticSpeciesSelector(fir));
      this.addChanceSelector(biomeDataBase, TraverseWorld.snowyConiferousForestBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.aridHighlandBiome, (new RandomSpeciesSelector()).add(acacia, 4).add(oak, 1).add(cactus, 1));
      this.addDensitySelector(biomeDataBase, TraverseWorld.aridHighlandBiome, this.scale(0.1D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.aridHighlandBiome, this.chance(0.2F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.badlandsBiome, (new RandomSpeciesSelector()).add(autumnBrown, 1).add(oak, 1));
      this.addDensitySelector(biomeDataBase, TraverseWorld.badlandsBiome, this.scale(0.03D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.badlandsBiome, this.chance(0.0625F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.birchForestedHillsBiome, new StaticSpeciesSelector(birch));
      this.addChanceSelector(biomeDataBase, TraverseWorld.birchForestedHillsBiome, this.chance(1.25F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.forestedHillsBiome, (new RandomSpeciesSelector()).add(oak, 4).add(birch, 2));
      this.addDensitySelector(biomeDataBase, TraverseWorld.forestedHillsBiome, this.scale(0.9D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.forestedHillsBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.lushHillsBiome, (new RandomSpeciesSelector()).add(oak, 4).add(swamp, 1));
      this.addDensitySelector(biomeDataBase, TraverseWorld.lushHillsBiome, this.scale(0.4D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.lushHillsBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.lushSwampBiome, (new RandomSpeciesSelector()).add(swamp, 4).add(oak, 1));
      this.addDensitySelector(biomeDataBase, TraverseWorld.lushSwampBiome, this.scale(0.6D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.lushSwampBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.meadowBiome, (new RandomSpeciesSelector()).add(oak, 2).add(birch, 1));
      this.addDensitySelector(biomeDataBase, TraverseWorld.meadowBiome, this.scale(0.01D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.meadowBiome, this.chance(0.5F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.rockyPlainsBiome, (new RandomSpeciesSelector()).add(oak, 2));
      this.addDensitySelector(biomeDataBase, TraverseWorld.rockyPlainsBiome, this.scale(0.01D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.rockyPlainsBiome, this.chance(0.05F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.rockyPlateauBiome, (new RandomSpeciesSelector()).add(oak, 2));
      this.addDensitySelector(biomeDataBase, TraverseWorld.rockyPlateauBiome, this.scale(0.01D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.rockyPlateauBiome, this.chance(0.5F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.thicketBiome, (new RandomSpeciesSelector()).add(oak, 4).add(darkOak, 2));
      this.addDensitySelector(biomeDataBase, TraverseWorld.thicketBiome, this.scale(1.5D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.thicketBiome, this.chance(1.0F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.woodlandsBiome, new StaticSpeciesSelector(oak));
      this.addDensitySelector(biomeDataBase, TraverseWorld.woodlandsBiome, this.scale(0.6D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.woodlandsBiome, this.chance(0.75F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.redDesertBiome, new StaticSpeciesSelector(cactus));
      this.addDensitySelector(biomeDataBase, TraverseWorld.redDesertBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.redDesertBiome, this.chance(0.05F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.mountainousDesertBiome, new StaticSpeciesSelector(cactus));
      this.addDensitySelector(biomeDataBase, TraverseWorld.mountainousDesertBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.mountainousDesertBiome, this.chance(0.05F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.miniJungleBiome, (new RandomSpeciesSelector()).add(oak, 1).add(miniJungle, 4));
      this.addDensitySelector(biomeDataBase, TraverseWorld.miniJungleBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.miniJungleBiome, this.chance(0.85F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.cliffsBiome, (new RandomSpeciesSelector()).add(oak, 2).add(4));
      this.addDensitySelector(biomeDataBase, TraverseWorld.cliffsBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.cliffsBiome, this.chance(0.5F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.glacierBiome, (new RandomSpeciesSelector()).add(spruce, 2).add(8));
      this.addDensitySelector(biomeDataBase, TraverseWorld.glacierBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.glacierBiome, this.chance(0.5F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.glacierSpikesBiome, (new RandomSpeciesSelector()).add(spruce, 2).add(8));
      this.addDensitySelector(biomeDataBase, TraverseWorld.glacierSpikesBiome, this.scale(2.0D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.glacierSpikesBiome, this.chance(0.5F));
      this.addSpeciesSelector(biomeDataBase, TraverseWorld.desertShrublandBiome, (new RandomSpeciesSelector()).add(oakShrub, 3).add(8));
      this.addDensitySelector(biomeDataBase, TraverseWorld.desertShrublandBiome, this.scale(0.800000011920929D));
      this.addChanceSelector(biomeDataBase, TraverseWorld.desertShrublandBiome, this.chance(0.6F));
      Biome.REGISTRY.forEach((biome) -> {
         if (biome.getRegistryName().getResourceDomain().equals("traverse")) {
            biomeDataBase.setCancelVanillaTreeGen(biome, true);
         }

      });
   }

   private void addSpeciesSelector(BiomeDataBase dbase, Biome biome, ISpeciesSelector selector) {
      dbase.setSpeciesSelector(biome, selector, Operation.REPLACE);
   }

   private void addDensitySelector(BiomeDataBase dbase, Biome biome, IDensitySelector selector) {
      dbase.setDensitySelector(biome, selector, Operation.REPLACE);
   }

   private void addChanceSelector(BiomeDataBase dbase, Biome biome, IChanceSelector selector) {
      dbase.setChanceSelector(biome, selector, Operation.REPLACE);
   }

   private IDensitySelector scale(double factor) {
      return (rand, noiseDensity) -> {
         return noiseDensity * factor;
      };
   }

   private IChanceSelector chance(float value) {
      if (value <= 0.0F) {
         return (rnd, spc, rad) -> {
            return EnumChance.CANCEL;
         };
      } else {
         return value >= 1.0F ? (rnd, spc, rad) -> {
            return EnumChance.OK;
         } : (rnd, spc, rad) -> {
            return rnd.nextFloat() < value ? EnumChance.OK : EnumChance.CANCEL;
         };
      }
   }
}
