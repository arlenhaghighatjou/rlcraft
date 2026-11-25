package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import java.util.function.Function;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public class BiomeDataBase {
   public static BiomeDataBase.BiomeEntry BADENTRY = new BiomeDataBase.BiomeEntry() {
      public void setChanceSelector(BiomePropertySelectors.IChanceSelector chanceSelector) {
      }

      public void setDensitySelector(BiomePropertySelectors.IDensitySelector densitySelector) {
      }

      public void setSpeciesSelector(BiomePropertySelectors.ISpeciesSelector speciesSelector) {
      }

      public void setCancelVanillaTreeGen(boolean cancel) {
      }

      public void setSubterraneanBiome(boolean is) {
      }
   };
   private final BiomeDataBase.BiomeEntry[][] table = new BiomeDataBase.BiomeEntry[16][];

   public BiomeDataBase() {
   }

   public BiomeDataBase.BiomeEntry getEntry(Biome biome) {
      if (biome != null) {
         int biomeId = Biome.getIdForBiome(biome);
         if (biomeId >= 0 && biomeId <= 255) {
            BiomeDataBase.BiomeEntry[] list = this.table[biomeId >> 4];
            if (list == null) {
               list = this.table[biomeId >> 4] = new BiomeDataBase.BiomeEntry[16];

               for(int i = 0; i < 16; ++i) {
                  list[i] = BADENTRY;
               }
            }

            BiomeDataBase.BiomeEntry entry = list[biomeId & 15];
            return entry != BADENTRY ? entry : (list[biomeId & 15] = new BiomeDataBase.BiomeEntry(biome, biomeId));
         }
      }

      return BADENTRY;
   }

   public void clear() {
      for(int i = 0; i < 16; ++i) {
         this.table[i] = null;
      }

   }

   public boolean isValid() {
      for(Biome biome : Biome.REGISTRY) {
         BiomeDataBase.BiomeEntry entry = this.getEntry(biome);
         if (entry.getBiome() != biome) {
            return false;
         }
      }

      return true;
   }

   public BiomePropertySelectors.ISpeciesSelector getSpecies(Biome biome) {
      return this.getEntry(biome).speciesSelector;
   }

   public BiomePropertySelectors.IChanceSelector getChance(Biome biome) {
      return this.getEntry(biome).chanceSelector;
   }

   public BiomePropertySelectors.IDensitySelector getDensity(Biome biome) {
      return this.getEntry(biome).densitySelector;
   }

   public boolean shouldCancelVanillaTreeGen(Biome biome) {
      return this.getEntry(biome).cancelVanillaTreeGen;
   }

   public float getForestness(Biome biome) {
      return this.getEntry(biome).getForestness();
   }

   public Function<Integer, Integer> getMultipass(Biome biome) {
      return this.getEntry(biome).getMultipass();
   }

   public BiomeDataBase setSpeciesSelector(Biome biome, BiomePropertySelectors.ISpeciesSelector selector, BiomeDataBase.Operation op) {
      if (selector != null) {
         BiomeDataBase.BiomeEntry entry = this.getEntry(biome);
         BiomePropertySelectors.ISpeciesSelector existing = entry.getSpeciesSelector();
         switch(op) {
         case REPLACE:
            entry.setSpeciesSelector(selector);
            break;
         case SPLICE_BEFORE:
            entry.setSpeciesSelector((pos, dirt, rnd) -> {
               BiomePropertySelectors.SpeciesSelection ss = selector.getSpecies(pos, dirt, rnd);
               return ss.isHandled() ? ss : existing.getSpecies(pos, dirt, rnd);
            });
            break;
         case SPLICE_AFTER:
            entry.setSpeciesSelector((pos, dirt, rnd) -> {
               BiomePropertySelectors.SpeciesSelection ss = existing.getSpecies(pos, dirt, rnd);
               return ss.isHandled() ? ss : selector.getSpecies(pos, dirt, rnd);
            });
         }
      }

      return this;
   }

   public BiomeDataBase setChanceSelector(Biome biome, BiomePropertySelectors.IChanceSelector selector, BiomeDataBase.Operation op) {
      if (selector != null) {
         BiomeDataBase.BiomeEntry entry = this.getEntry(biome);
         BiomePropertySelectors.IChanceSelector existing = entry.getChanceSelector();
         switch(op) {
         case REPLACE:
            entry.setChanceSelector(selector);
            break;
         case SPLICE_BEFORE:
            entry.setChanceSelector((rnd, spc, rad) -> {
               BiomePropertySelectors.EnumChance c = selector.getChance(rnd, spc, rad);
               return c != BiomePropertySelectors.EnumChance.UNHANDLED ? c : existing.getChance(rnd, spc, rad);
            });
            break;
         case SPLICE_AFTER:
            entry.setChanceSelector((rnd, spc, rad) -> {
               BiomePropertySelectors.EnumChance c = existing.getChance(rnd, spc, rad);
               return c != BiomePropertySelectors.EnumChance.UNHANDLED ? c : selector.getChance(rnd, spc, rad);
            });
         }
      }

      return this;
   }

   public BiomeDataBase setDensitySelector(Biome biome, BiomePropertySelectors.IDensitySelector selector, BiomeDataBase.Operation op) {
      if (selector != null) {
         BiomeDataBase.BiomeEntry entry = this.getEntry(biome);
         BiomePropertySelectors.IDensitySelector existing = entry.getDensitySelector();
         switch(op) {
         case REPLACE:
            entry.setDensitySelector(selector);
            break;
         case SPLICE_BEFORE:
            entry.setDensitySelector((rnd, nd) -> {
               double d = selector.getDensity(rnd, nd);
               return d >= 0.0D ? d : existing.getDensity(rnd, nd);
            });
            break;
         case SPLICE_AFTER:
            entry.setDensitySelector((rnd, nd) -> {
               double d = existing.getDensity(rnd, nd);
               return d >= 0.0D ? d : selector.getDensity(rnd, nd);
            });
         }
      }

      return this;
   }

   public BiomeDataBase setCancelVanillaTreeGen(Biome biome, boolean cancel) {
      this.getEntry(biome).setCancelVanillaTreeGen(cancel);
      return this;
   }

   public BiomeDataBase setIsSubterranean(Biome biome, boolean is) {
      this.getEntry(biome).setSubterraneanBiome(is);
      return this;
   }

   public BiomeDataBase setForestness(Biome biome, float forestness) {
      this.getEntry(biome).setForestness(Math.max(forestness, ModConfigs.seedMinForestness));
      return this;
   }

   public BiomeDataBase setMultipass(Biome biome, Function<Integer, Integer> multipass) {
      this.getEntry(biome).setMultipass(multipass);
      return this;
   }

   public static class BiomeEntry {
      private final Biome biome;
      private final int biomeId;
      private BiomePropertySelectors.IChanceSelector chanceSelector = (rnd, spc, rad) -> {
         return BiomePropertySelectors.EnumChance.UNHANDLED;
      };
      private BiomePropertySelectors.IDensitySelector densitySelector = (rnd, nd) -> {
         return -1.0D;
      };
      private BiomePropertySelectors.ISpeciesSelector speciesSelector = (pos, dirt, rnd) -> {
         return new BiomePropertySelectors.SpeciesSelection();
      };
      private boolean cancelVanillaTreeGen = false;
      private boolean isSubterranean = false;
      private float forestness = 0.0F;
      private static final Function<Integer, Integer> defaultMultipass = (pass) -> {
         return pass.intValue() == 0 ? 0 : -1;
      };
      private Function<Integer, Integer> multipass;

      public BiomeEntry() {
         this.multipass = defaultMultipass;
         this.biome = Biomes.DEFAULT;
         this.biomeId = -1;
      }

      public BiomeEntry(Biome biome, int biomeId) {
         this.multipass = defaultMultipass;
         this.biome = biome;
         this.biomeId = biomeId;
      }

      public Biome getBiome() {
         return this.biome;
      }

      public int getBiomeId() {
         return this.biomeId;
      }

      public BiomePropertySelectors.IChanceSelector getChanceSelector() {
         return this.chanceSelector;
      }

      public BiomePropertySelectors.IDensitySelector getDensitySelector() {
         return this.densitySelector;
      }

      public BiomePropertySelectors.ISpeciesSelector getSpeciesSelector() {
         return this.speciesSelector;
      }

      public void setChanceSelector(BiomePropertySelectors.IChanceSelector chanceSelector) {
         this.chanceSelector = chanceSelector;
      }

      public void setDensitySelector(BiomePropertySelectors.IDensitySelector densitySelector) {
         this.densitySelector = densitySelector;
      }

      public void setSpeciesSelector(BiomePropertySelectors.ISpeciesSelector speciesSelector) {
         this.speciesSelector = speciesSelector;
      }

      public void setCancelVanillaTreeGen(boolean cancel) {
         this.cancelVanillaTreeGen = cancel;
      }

      public void setSubterraneanBiome(boolean is) {
         this.isSubterranean = is;
      }

      public boolean shouldCancelVanillaTreeGen() {
         return this.cancelVanillaTreeGen;
      }

      public boolean isSubterraneanBiome() {
         return this.isSubterranean;
      }

      public void setForestness(float forestness) {
         this.forestness = forestness;
      }

      public float getForestness() {
         return this.forestness;
      }

      public void setMultipass(Function<Integer, Integer> multipass) {
         this.multipass = multipass;
      }

      public Function<Integer, Integer> getMultipass() {
         return this.multipass;
      }
   }

   public static enum Operation {
      REPLACE,
      SPLICE_BEFORE,
      SPLICE_AFTER;

      private Operation() {
      }
   }
}
