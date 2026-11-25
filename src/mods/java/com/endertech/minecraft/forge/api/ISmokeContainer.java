package com.endertech.minecraft.forge.api;

import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.units.IRelatedUnit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISmokeContainer extends IRelatedUnit {
   ColorARGB getColor();

   boolean isChimney(IBlockState var1);

   boolean isVent(IBlockState var1);

   boolean isPump(IBlockState var1);

   boolean isActive(World var1, BlockPos var2);
}
