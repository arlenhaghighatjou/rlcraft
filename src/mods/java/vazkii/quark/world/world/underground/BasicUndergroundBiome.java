package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BasicUndergroundBiome extends UndergroundBiome {
   public IBlockState floorState;
   public IBlockState ceilingState;
   public IBlockState wallState;
   public final boolean mimicInside;

   public BasicUndergroundBiome(IBlockState floorState, IBlockState ceilingState, IBlockState wallState) {
      this(floorState, ceilingState, wallState, false);
   }

   public BasicUndergroundBiome(IBlockState floorState, IBlockState ceilingState, IBlockState wallState, boolean mimicInside) {
      this.floorState = floorState;
      this.ceilingState = ceilingState;
      this.wallState = wallState;
      this.mimicInside = mimicInside;
   }

   public void fillFloor(World world, BlockPos pos, IBlockState state) {
      if (this.floorState != null) {
         world.setBlockState(pos, this.floorState, 2);
      }

   }

   public void fillCeiling(World world, BlockPos pos, IBlockState state) {
      if (this.ceilingState != null) {
         world.setBlockState(pos, this.ceilingState, 2);
      }

   }

   public void fillWall(World world, BlockPos pos, IBlockState state) {
      if (this.wallState != null) {
         world.setBlockState(pos, this.wallState, 2);
      }

   }

   public void fillInside(World world, BlockPos pos, IBlockState state) {
      if (this.mimicInside) {
         this.fillWall(world, pos, state);
      }

   }
}
