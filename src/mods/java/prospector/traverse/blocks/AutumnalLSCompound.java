package prospector.traverse.blocks;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;
import prospector.traverse.blocks.base.BlockTraverseLeaves;
import prospector.traverse.blocks.base.BlockTraverseSapling;
import prospector.traverse.world.features.TraverseTreeGenerator;

public class AutumnalLSCompound {
   public String name;
   public AutumnalLSCompound.LSLeaves lsLeaves;
   public AutumnalLSCompound.LSSapling lsSapling;

   public AutumnalLSCompound(String colour) {
      this.name = colour + "_autumnal";
      this.lsLeaves = new AutumnalLSCompound.LSLeaves();
      this.lsSapling = new AutumnalLSCompound.LSSapling();
   }

   public class LSLeaves extends BlockTraverseLeaves {
      public LSLeaves() {
         super(AutumnalLSCompound.this.name, (Item)null, 20);
      }

      public Item getItemDropped(IBlockState state, Random rand, int fortune) {
         return Item.getItemFromBlock(AutumnalLSCompound.this.lsSapling);
      }

      protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
         if (worldIn.rand.nextInt(chance) == 0) {
            spawnAsEntity(worldIn, pos, new ItemStack(Items.APPLE));
         }

      }
   }

   public class LSSapling extends BlockTraverseSapling {
      public LSSapling() {
         super(AutumnalLSCompound.this.name, new TraverseTreeGenerator(false));
      }

      public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
         if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
            worldIn.setBlockToAir(pos);
            if (!(new TraverseTreeGenerator(true, 4, TraverseTreeGenerator.OAK_LOG, AutumnalLSCompound.this.lsLeaves.getDefaultState())).generate(worldIn, rand, pos)) {
               worldIn.setBlockState(pos, state);
            }

         }
      }
   }
}
