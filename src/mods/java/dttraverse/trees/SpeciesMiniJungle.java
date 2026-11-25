package dttraverse.trees;

import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenCocoa;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenVine;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.trees.TreeFamily.ISpeciesLocationOverride;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import prospector.traverse.world.TraverseWorld;

public final class SpeciesMiniJungle extends Species {
   public SpeciesMiniJungle(TreeFamily treeFamily) {
      super(new ResourceLocation("dttraverse", "mini_jungle"), treeFamily);
      this.setBasicGrowingParameters(0.4F, 9.0F, 2, 4, 0.9F);
      this.envFactor(Type.HOT, 1.15F);
      this.envFactor(Type.DRY, 1.2F);
      this.envFactor(Type.FOREST, 1.15F);
      this.setGrowthLogicKit(treeFamily.getCommonSpecies().getGrowthLogicKit());
      this.setupStandardSeedDropping();
      this.addGenFeature(new FeatureGenCocoa());
      this.addGenFeature((new FeatureGenVine()).setQuantity(16).setMaxLength(6));
      this.getFamily().addSpeciesLocationOverride((world, trunkPos) -> {
         return (Species)(world.getBiome(trunkPos).equals(TraverseWorld.miniJungleBiome) ? this : NULLSPECIES);
      });
   }

   public boolean isBiomePerfect(Biome biome) {
      return BiomeDictionary.hasType(biome, Type.JUNGLE);
   }

   public ItemStack getSeedStack(int qty) {
      return this.getFamily().getCommonSpecies().getSeedStack(qty);
   }

   public Seed getSeed() {
      return this.getFamily().getCommonSpecies().getSeed();
   }
}
