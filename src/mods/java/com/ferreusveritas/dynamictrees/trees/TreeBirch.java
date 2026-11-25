package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.items.Seed;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeBirch extends TreeFamilyVanilla {
   Species tallSpecies;

   private static boolean isMutatedBirchForest(Biome biome) {
      return Species.isOneOfBiomes(biome, Biomes.MUTATED_BIRCH_FOREST, Biomes.MUTATED_BIRCH_FOREST_HILLS);
   }

   public TreeBirch() {
      super(EnumType.BIRCH);
      this.hasConiferVariants = true;
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockOldLeaf && state.getValue(BlockOldLeaf.VARIANT) == EnumType.BIRCH;
      });
      this.addSpeciesLocationOverride((world, trunkPos) -> {
         return isMutatedBirchForest(world.getBiome(trunkPos)) ? this.tallSpecies : Species.NULLSPECIES;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeBirch.SpeciesBirch(this));
      this.tallSpecies = new TreeBirch.SpeciesTallBirch(this);
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      super.registerSpecies(speciesRegistry);
      speciesRegistry.register(this.tallSpecies);
   }

   public class SpeciesBirch extends Species {
      SpeciesBirch(TreeFamily treeFamily) {
         this(treeFamily.getName(), treeFamily);
      }

      public SpeciesBirch(ResourceLocation name, TreeFamily treeFamily) {
         super(name, treeFamily);
         this.setBasicGrowingParameters(0.1F, 14.0F, 4, 4, 1.25F);
         this.envFactor(Type.COLD, 0.75F);
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.DRY, 0.5F);
         this.envFactor(Type.FOREST, 1.05F);
         this.setupStandardSeedDropping();
      }

      public boolean isBiomePerfect(Biome biome) {
         return isOneOfBiomes(biome, new Biome[]{Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS});
      }

      public boolean rot(World world, BlockPos pos, int neighborCount, int radius, EaglercraftRandom random, boolean rapid) {
         if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
            if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
               world.setBlockState(pos, Blocks.BROWN_MUSHROOM.getDefaultState());
               world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState(), 3);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public class SpeciesTallBirch extends TreeBirch.SpeciesBirch {
      public SpeciesTallBirch(TreeFamily treeFamily) {
         super(new ResourceLocation("dynamictrees", "tallbirch"), treeFamily);
         this.setBasicGrowingParameters(0.08F, 24.0F, 7, 7, 1.3F);
         this.setSoilLongevity(12);
      }

      public boolean isBiomePerfect(Biome biome) {
         return TreeBirch.isMutatedBirchForest(biome);
      }

      public ItemStack getSeedStack(int qty) {
         return TreeBirch.this.getCommonSpecies().getSeedStack(qty);
      }

      public Seed getSeed() {
         return TreeBirch.this.getCommonSpecies().getSeed();
      }
   }
}
