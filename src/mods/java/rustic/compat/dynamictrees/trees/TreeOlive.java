package rustic.compat.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.trees.TreeFamily.IConnectable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.common.blocks.BlockLeavesRustic;
import rustic.common.blocks.BlockLogRustic;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.compat.dynamictrees.DropCreatorFruit;
import rustic.compat.dynamictrees.DynamicTreesCompat;

public class TreeOlive extends TreeFamily {
   public TreeOlive() {
      super(new ResourceLocation("rustic", "olive"));
      IBlockState primLog = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.OLIVE);
      this.setPrimitiveLog(primLog, new ItemStack(ModBlocks.LOG, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()));
      DynamicTreesCompat.oliveLeavesProperties.setTree(this);
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockLeavesRustic && state.getValue(BlockLeavesRustic.VARIANT) == BlockPlanksRustic.EnumType.OLIVE;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeOlive.SpeciesOlive(this));
   }

   public class SpeciesOlive extends Species {
      SpeciesOlive(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily, DynamicTreesCompat.oliveLeavesProperties);
         this.setBasicGrowingParameters(0.3F, 12.0F, this.upProbability, this.lowestBranchHeight, 1.0F);
         this.envFactor(Type.COLD, 0.75F);
         this.envFactor(Type.SNOWY, 0.25F);
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.PLAINS, 1.05F);
         this.generateSeed();
         this.setupStandardSeedDropping();
         this.addDropCreator(new DropCreatorFruit(ModItems.OLIVES, 18));
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.PLAINS);
      }
   }
}
