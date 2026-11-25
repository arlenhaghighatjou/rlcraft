package dttraverse.trees;

import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenBush;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import prospector.traverse.world.TraverseWorld;

public final class SpeciesOakShrub extends Species {
   public SpeciesOakShrub(TreeFamily treeFamily) {
      super(new ResourceLocation("dttraverse", "oak_shrub"), treeFamily);
      this.setStandardSoils();
      this.addGenFeature(new FeatureGenBush(), 1);
   }

   public boolean isBiomePerfect(Biome biome) {
      return isOneOfBiomes(biome, new Biome[]{TraverseWorld.desertShrublandBiome});
   }

   public ItemStack getSeedStack(int qty) {
      return this.getFamily().getCommonSpecies().getSeedStack(qty);
   }

   public Seed getSeed() {
      return this.getFamily().getCommonSpecies().getSeed();
   }
}
