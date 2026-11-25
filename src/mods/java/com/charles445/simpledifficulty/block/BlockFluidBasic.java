package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.api.SDFluids;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidBasic extends BlockFluidClassic {
   public BlockFluidBasic(Fluid fluid, Material material) {
      super(fluid, material);
      this.setRegistryName(fluid.getName());
      this.setUnlocalizedName(this.getRegistryName().toString());
      SDFluids.fluidBlocks.put(fluid.getName(), this);
   }

   public int getLightValue(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
      return 1;
   }

   public int getLightValue(IBlockState state) {
      return 1;
   }
}
