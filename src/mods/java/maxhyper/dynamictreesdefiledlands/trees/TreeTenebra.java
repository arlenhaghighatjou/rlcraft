package maxhyper.dynamictreesdefiledlands.trees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import lykrast.defiledlands.common.init.ModBlocks;
import maxhyper.dynamictreesdefiledlands.ModContent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TreeTenebra extends TreeFamily {
   public static Block logBlock = ModBlocks.tenebraLog;

   public TreeTenebra() {
      super(new ResourceLocation("dynamictreesdefiledlands", "tenebra"));
      this.setPrimitiveLog(logBlock.getDefaultState());
      ModContent.dyingLeavesProperties.setTree(this);
   }

   public ItemStack getPrimitiveLogItemStack(int qty) {
      ItemStack stack = new ItemStack(logBlock, 1, 0);
      stack.setCount(MathHelper.clamp(qty, 0, 64));
      return stack;
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeTenebra.SpeciesTenebra(this));
   }

   public class SpeciesTenebra extends Species {
      SpeciesTenebra(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily, ModContent.dyingLeavesProperties);
         this.setSoilLongevity(3);
         this.setBasicGrowingParameters(0.3F, 16.0F, 8, this.lowestBranchHeight, 0.65F);
         this.generateSeed();
         this.setupStandardSeedDropping();
         this.setRequiresTileEntity(true);
         ModContent.dyingLeavesProperties.setTree(treeFamily);
      }

      protected void setStandardSoils() {
         this.addAcceptableSoils(new String[]{"dirtlike", ModContent.CORRUPTDIRTLIKE});
      }

      public BlockRooty getRootyBlock(World world, BlockPos rootPos) {
         return DirtHelper.isSoilAcceptable(world.getBlockState(rootPos).getBlock(), DirtHelper.getSoilFlags(new String[]{ModContent.CORRUPTDIRTLIKE})) ? ModContent.rootyDefiledDirt : super.getRootyBlock(world, rootPos);
      }

      protected int[] customDirectionManipulation(World world, BlockPos pos, int radius, GrowSignal signal, int[] probMap) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            int var10001 = dir.ordinal();
            probMap[var10001] *= 3;
         }

         if (radius > 1) {
            for(EnumFacing dir : EnumFacing.values()) {
               if (!TreeHelper.isBranch(world.getBlockState(pos.offset(dir))) || TreeHelper.getRadius(world, pos.offset(dir)) <= 1 && TreeHelper.getRadius(world, pos) > 2) {
                  if (world.rand.nextFloat() < 0.9F) {
                     probMap[dir.ordinal()] = 0;
                  } else {
                     signal.energy = 4.0F;
                  }
               }
            }
         }

         probMap[signal.dir.getOpposite().ordinal()] = 0;
         probMap[EnumFacing.DOWN.ordinal()] = 0;
         return probMap;
      }
   }
}
