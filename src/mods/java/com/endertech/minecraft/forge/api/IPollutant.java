package com.endertech.minecraft.forge.api;

import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPollutant {
   boolean affectsPollutionLevel(World var1, BlockPos var2, IBlockState var3);

   boolean canAffectBlock(World var1, BlockPos var2, Optional<EnumFacing> var3, boolean var4);

   boolean canAffectEntity(Entity var1, boolean var2);

   /** @deprecated */
   @Deprecated
   boolean canPassThrough(World var1, BlockPos var2);

   boolean canPassThrough(World var1, BlockPos var2, EnumFacing var3, EnumFacing var4);

   int getCarriedPollutionAmount(IBlockState var1);

   int getPollutionCapacity();

   boolean pump(World var1, BlockPos var2);

   int pump(World var1, BlockPos var2, int var3);

   int pumpEntitiesAt(World var1, BlockPos var2, int var3);

   boolean push(World var1, BlockPos var2, EnumFacing var3);

   boolean spend(World var1, BlockPos var2);

   int spend(World var1, BlockPos var2, int var3);

   IPollutant.Type getPollutantType();

   public static enum Type {
      AIR,
      WATER,
      SOIL;

      private Type() {
      }
   }
}
