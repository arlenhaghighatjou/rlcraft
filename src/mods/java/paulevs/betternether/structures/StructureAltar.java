package paulevs.betternether.structures;

import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockCincinnasitFireBowl;
import paulevs.betternether.blocks.BlocksRegister;

public class StructureAltar implements IStructureWorld {
   public StructureAltar() {
   }

   public void generate(World world, BlockPos pos, Rotation rotation) {
      if ((world.getBlockState(pos.down()).getBlock() instanceof BlockNetherrack || world.getBlockState(pos.down()).getBlock() instanceof BlockSoulSand) && world.getBlockState(pos).getBlock() == Blocks.AIR && world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
         IBlockState pillar = BlocksRegister.BLOCK_CINCINNASITE_WALL.getDefaultState();
         IBlockState head = Blocks.SKULL.getDefaultState();
         this.setBlockAndNotifyAdequately(world, pos, BlocksRegister.BLOCK_CINCINNASITE_PILLAR.getDefaultState());
         this.setBlockAndNotifyAdequately(world, pos.up(), BlocksRegister.BLOCK_CINCINNASITE_FIRE_BOWL.getDefaultState().withProperty(BlockCincinnasitFireBowl.STATE, Boolean.valueOf(true)));
         this.setBlockAndNotifyAdequately(world, pos.north().east(), pillar);
         this.setBlockAndNotifyAdequately(world, pos.north().west(), pillar);
         this.setBlockAndNotifyAdequately(world, pos.south().east(), pillar);
         this.setBlockAndNotifyAdequately(world, pos.south().west(), pillar);
         pos = pos.down();
         if (!this.isTopSolid(world, pos.north().east())) {
            this.setBlockAndNotifyAdequately(world, pos.north().east(), Blocks.NETHERRACK.getDefaultState());
         }

         if (!this.isTopSolid(world, pos.north().west())) {
            this.setBlockAndNotifyAdequately(world, pos.north().west(), Blocks.NETHERRACK.getDefaultState());
         }

         if (!this.isTopSolid(world, pos.south().east())) {
            this.setBlockAndNotifyAdequately(world, pos.south().east(), Blocks.NETHERRACK.getDefaultState());
         }

         if (!this.isTopSolid(world, pos.south().west())) {
            this.setBlockAndNotifyAdequately(world, pos.south().west(), Blocks.NETHERRACK.getDefaultState());
         }
      }

   }
}
