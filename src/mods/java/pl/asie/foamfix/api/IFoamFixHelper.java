package pl.asie.foamfix.api;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public interface IFoamFixHelper {
   BlockStateContainer createBlockState(Block var1, IProperty<?>... var2);

   BlockStateContainer createExtendedBlockState(Block var1, IProperty<?>[] var2, IUnlistedProperty<?>[] var3);

   public static class Default implements IFoamFixHelper {
      public Default() {
      }

      public BlockStateContainer createBlockState(Block block, IProperty<?>... properties) {
         return new BlockStateContainer(block, properties);
      }

      public BlockStateContainer createExtendedBlockState(Block block, IProperty<?>[] properties, IUnlistedProperty<?>[] unlistedProperties) {
         return new ExtendedBlockState(block, properties, unlistedProperties);
      }
   }
}
