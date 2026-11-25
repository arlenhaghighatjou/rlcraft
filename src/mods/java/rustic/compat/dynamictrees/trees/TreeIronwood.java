package rustic.compat.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFlareBottom;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenRoots;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.trees.TreeFamily.IConnectable;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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

public class TreeIronwood extends TreeFamily {
   BlockSurfaceRoot surfaceRootBlock;

   public TreeIronwood() {
      super(new ResourceLocation("rustic", "ironwood"));
      IBlockState primLog = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.IRONWOOD);
      this.setPrimitiveLog(primLog, new ItemStack(ModBlocks.LOG, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
      DynamicTreesCompat.ironwoodLeavesProperties.setTree(this);
      this.surfaceRootBlock = new BlockSurfaceRoot(Material.WOOD, this.getName() + "root");
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockLeavesRustic && state.getValue(BlockLeavesRustic.VARIANT) == BlockPlanksRustic.EnumType.IRONWOOD;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeIronwood.SpeciesIronwood(this));
   }

   public List<Block> getRegisterableBlocks(List<Block> blockList) {
      blockList.add(this.surfaceRootBlock);
      return super.getRegisterableBlocks(blockList);
   }

   public boolean isThick() {
      return true;
   }

   public BlockSurfaceRoot getSurfaceRoots() {
      return this.surfaceRootBlock;
   }

   public class SpeciesIronwood extends Species {
      SpeciesIronwood(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily, DynamicTreesCompat.ironwoodLeavesProperties);
         this.setBasicGrowingParameters(0.4F, 14.0F, 4, 4, 1.0F);
         this.envFactor(Type.COLD, 0.5F);
         this.envFactor(Type.DRY, 0.5F);
         this.envFactor(Type.HOT, 0.75F);
         this.envFactor(Type.FOREST, 1.05F);
         this.generateSeed();
         this.setupStandardSeedDropping();
         this.addDropCreator(new DropCreatorFruit(ModItems.IRONBERRIES, 48));
         this.addGenFeature(new FeatureGenClearVolume(6));
         this.addGenFeature(new FeatureGenFlareBottom());
         this.addGenFeature(new FeatureGenMound(5));
         this.addGenFeature((new FeatureGenRoots(11)).setScaler(this.getRootScaler()));
      }

      protected BiFunction<Integer, Integer, Integer> getRootScaler() {
         return (inRadius, trunkRadius) -> {
            float scale = MathHelper.clamp(trunkRadius.intValue() >= 11 ? (float)trunkRadius.intValue() / 20.0F : 0.0F, 0.0F, 1.0F);
            return (int)((float)inRadius.intValue() * scale);
         };
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.FOREST);
      }

      public boolean isThick() {
         return true;
      }
   }
}
