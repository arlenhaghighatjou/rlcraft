package dttraverse.trees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenConiferTopper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import dttraverse.DynamicTreesTraverse;
import dttraverse.ModContent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.GameRegistry;
import prospector.traverse.init.TraverseBlocks;

public final class TreeFir extends TreeFamily {
   public TreeFir() {
      super(new ResourceLocation("dttraverse", "fir"));
      Block firLog = (Block)TraverseBlocks.blocks.get("fir_log");
      Item firLeavesItem = new ItemBlock(firLog);
      firLeavesItem.setRegistryName(firLog.getRegistryName());
      GameRegistry.findRegistry(Item.class).register(firLeavesItem);
      IBlockState primLog = firLog.getDefaultState();
      DynamicTreesTraverse.logger.info("FIRLOG:" + firLog + " " + new ItemStack(firLog, 1));
      this.setPrimitiveLog(primLog, new ItemStack(Item.getItemFromBlock(firLog), 1));
      this.hasConiferVariants = true;
      ModContent.firLeavesProperties.setTree(this);
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeFir.SpeciesFir(this));
   }

   public static final class SpeciesFir extends Species {
      SpeciesFir(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily, ModContent.firLeavesProperties);
         this.setBasicGrowingParameters(0.3F, 26.0F, 7, 6, 0.9F);
         this.setGrowthLogicKit(TreeRegistry.findGrowthLogicKit("conifer"));
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.DRY, 0.25F);
         this.envFactor(Type.WET, 0.75F);
         this.generateSeed();
         this.setupStandardSeedDropping();
         this.addGenFeature(new FeatureGenConiferTopper(this.getLeavesProperties()));
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.CONIFEROUS);
      }
   }
}
