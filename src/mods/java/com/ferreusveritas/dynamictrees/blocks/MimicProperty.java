package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IUnlistedProperty;

public class MimicProperty implements IUnlistedProperty<IBlockState> {
   public static final MimicProperty MIMIC = new MimicProperty("mimic");
   private static int dirtLikeFlags;
   private static int sandLikeFlags;
   public static final Material[] materialOrderGrassy = new Material[]{Material.GRASS, Material.GROUND};
   private static final BlockPos[] searchPattern = new BlockPos[13];
   private final String name;

   public static void setupSoilFlags() {
      dirtLikeFlags = DirtHelper.getSoilFlags("dirtlike");
      sandLikeFlags = DirtHelper.getSoilFlags("sandlike");
      int[] dMap = new int[]{0, -1, 1};
      int i = 0;

      for(int depth : dMap) {
         for(EnumFacing dir : EnumFacing.HORIZONTALS) {
            searchPattern[i++] = BlockPos.ORIGIN.offset(dir).down(depth);
         }
      }

      searchPattern[i++] = BlockPos.ORIGIN.offset(EnumFacing.DOWN);
   }

   public MimicProperty(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(IBlockState value) {
      return value != null;
   }

   public Class<IBlockState> getType() {
      return IBlockState.class;
   }

   public String valueToString(IBlockState value) {
      return value.toString();
   }

   public static IBlockState getGenericMimic(IBlockAccess access, BlockPos pos, Material[] materialOrder, int soilFlags, IBlockState fallBack) {
      if (materialOrder == null) {
         for(BlockPos offset : searchPattern) {
            IBlockState soil = access.getBlockState(pos.add(offset));
            if (DirtHelper.isSoilAcceptable(soil.getBlock(), soilFlags) && !(soil.getBlock() instanceof MimicProperty.IMimic)) {
               return soil;
            }
         }
      } else {
         IBlockState[] cache = new IBlockState[searchPattern.length];

         for(Material material : materialOrder) {
            int i = 0;

            for(BlockPos offset : searchPattern) {
               if (cache[i] == null) {
                  cache[i] = access.getBlockState(pos.add(offset));
               }

               IBlockState soil = cache[i];
               if (soil.getMaterial() == material && DirtHelper.isSoilAcceptable(soil.getBlock(), soilFlags) && !(soil.getBlock() instanceof MimicProperty.IMimic)) {
                  return soil;
               }

               ++i;
            }
         }
      }

      return fallBack;
   }

   public static IBlockState getDirtMimic(IBlockAccess access, BlockPos pos) {
      return getGenericMimic(access, pos, materialOrderGrassy, dirtLikeFlags, ModBlocks.blockStates.dirt);
   }

   public static IBlockState getSandMimic(IBlockAccess access, BlockPos pos) {
      return getGenericMimic(access, pos, (Material[])null, sandLikeFlags, ModBlocks.blockStates.sand);
   }

   public interface IMimic {
      IBlockState getMimic(IBlockAccess var1, BlockPos var2);
   }
}
