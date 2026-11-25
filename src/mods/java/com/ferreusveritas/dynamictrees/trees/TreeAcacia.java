package com.ferreusveritas.dynamictrees.trees;

import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class TreeAcacia extends TreeFamilyVanilla {
   public TreeAcacia() {
      super(EnumType.ACACIA);
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockNewLeaf && state.getValue(BlockNewLeaf.VARIANT) == EnumType.ACACIA;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeAcacia.SpeciesAcacia(this));
   }

   public class SpeciesAcacia extends Species {
      SpeciesAcacia(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
         this.setBasicGrowingParameters(0.15F, 12.0F, 0, 3, 0.7F);
         this.envFactor(Type.COLD, 0.25F);
         this.envFactor(Type.NETHER, 0.75F);
         this.envFactor(Type.WET, 0.75F);
         this.setupStandardSeedDropping();
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.SAVANNA);
      }

      protected void setStandardSoils() {
         this.addAcceptableSoils(new String[]{"dirtlike", "hardclaylike"});
      }
   }
}
