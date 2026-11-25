package com.ferreusveritas.dynamictrees.api.worldgen;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BiomePropertySelectors {
   public BiomePropertySelectors() {
   }

   public static enum EnumChance {
      OK,
      CANCEL,
      UNHANDLED;

      private EnumChance() {
      }
   }

   public interface IChanceSelector {
      BiomePropertySelectors.EnumChance getChance(EaglercraftRandom var1, @Nonnull Species var2, int var3);
   }

   public interface IDensitySelector {
      double getDensity(EaglercraftRandom var1, double var2);
   }

   public interface ISpeciesSelector {
      BiomePropertySelectors.SpeciesSelection getSpecies(BlockPos var1, IBlockState var2, EaglercraftRandom var3);
   }

   public static class RandomSpeciesSelector implements BiomePropertySelectors.ISpeciesSelector {
      ArrayList<BiomePropertySelectors.RandomSpeciesSelector.Entry> decisionTable = new ArrayList();
      int totalWeight;

      public RandomSpeciesSelector() {
      }

      public int getSize() {
         return this.decisionTable.size();
      }

      public BiomePropertySelectors.RandomSpeciesSelector add(@Nonnull Species species, int weight) {
         this.decisionTable.add(new BiomePropertySelectors.RandomSpeciesSelector.Entry(new BiomePropertySelectors.SpeciesSelection(species), weight));
         this.totalWeight += weight;
         return this;
      }

      public BiomePropertySelectors.RandomSpeciesSelector add(int weight) {
         this.decisionTable.add(new BiomePropertySelectors.RandomSpeciesSelector.Entry(new BiomePropertySelectors.SpeciesSelection(), weight));
         this.totalWeight += weight;
         return this;
      }

      public BiomePropertySelectors.SpeciesSelection getSpecies(BlockPos pos, IBlockState dirt, EaglercraftRandom random) {
         int chance = random.nextInt(this.totalWeight);

         for(BiomePropertySelectors.RandomSpeciesSelector.Entry entry : this.decisionTable) {
            if (chance < entry.weight) {
               return entry.decision;
            }

            chance -= entry.weight;
         }

         return ((BiomePropertySelectors.RandomSpeciesSelector.Entry)this.decisionTable.get(this.decisionTable.size() - 1)).decision;
      }

      private class Entry {
         public BiomePropertySelectors.SpeciesSelection decision;
         public int weight;

         public Entry(BiomePropertySelectors.SpeciesSelection d, int w) {
            this.decision = d;
            this.weight = w;
         }
      }
   }

   public static class SpeciesSelection {
      private final boolean handled;
      private final Species species;

      public SpeciesSelection() {
         this.handled = false;
         this.species = Species.NULLSPECIES;
      }

      public SpeciesSelection(@Nonnull Species species) {
         this.species = species;
         this.handled = true;
      }

      public boolean isHandled() {
         return this.handled;
      }

      public Species getSpecies() {
         return this.species;
      }
   }

   public static class StaticSpeciesSelector implements BiomePropertySelectors.ISpeciesSelector {
      final BiomePropertySelectors.SpeciesSelection decision;

      public StaticSpeciesSelector(BiomePropertySelectors.SpeciesSelection decision) {
         this.decision = decision;
      }

      public StaticSpeciesSelector(@Nonnull Species species) {
         this(new BiomePropertySelectors.SpeciesSelection(species));
      }

      public StaticSpeciesSelector() {
         this(new BiomePropertySelectors.SpeciesSelection());
      }

      public BiomePropertySelectors.SpeciesSelection getSpecies(BlockPos pos, IBlockState dirt, EaglercraftRandom random) {
         return this.decision;
      }
   }
}
