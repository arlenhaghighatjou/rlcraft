package prospector.traverse.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import prospector.traverse.blocks.base.BlockTraverseLeaves;
import prospector.traverse.blocks.base.BlockTraverseSapling;
import prospector.traverse.world.features.TraverseTreeGenerator;
import prospector.traverse.world.features.WorldGenFirTree;

public class FirLSCompound {
   public FirLSCompound.LSLeaves lsLeaves = new FirLSCompound.LSLeaves();
   public FirLSCompound.LSSapling lsSapling = new FirLSCompound.LSSapling();

   public FirLSCompound() {
   }

   public class LSLeaves extends BlockTraverseLeaves {
      public LSLeaves() {
         super("fir", (Item)null, 60);
      }

      public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
         return Item.getItemFromBlock(FirLSCompound.this.lsSapling);
      }
   }

   public class LSSapling extends BlockTraverseSapling {
      public LSSapling() {
         super("fir", new TraverseTreeGenerator(false));
      }

      public void generateTree(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
         if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
            worldIn.setBlockToAir(pos);
            if (!(new WorldGenFirTree(true)).generate(worldIn, rand, pos)) {
               worldIn.setBlockState(pos, state);
            }

         }
      }
   }
}
