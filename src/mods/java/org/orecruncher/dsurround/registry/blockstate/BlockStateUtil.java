package org.orecruncher.dsurround.registry.blockstate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.IDataAccessor;
import org.orecruncher.dsurround.registry.RegistryManager;

public final class BlockStateUtil {
   public BlockStateUtil() {
   }

   @Nonnull
   public static <T extends BlockStateData> T getStateData(@Nonnull IBlockState state) {
      IDataAccessor<T> accessor = (IDataAccessor)state;
      T result = accessor.getData();
      if (result == null) {
         RegistryManager.BLOCK.reload();
         result = accessor.getData();
         if (result == null) {
            ModBase.log().warn("Unable to find BlockStateData for state [%s]", new Object[]{state.toString()});
            result = BlockStateData.DEFAULT;
            accessor.setData(result);
         }
      }

      return result;
   }

   static <T extends BlockStateData> T getStateDataRaw(@Nonnull IBlockState state) {
      return (T)(((IDataAccessor)state).getData());
   }

   public static <T extends BlockStateData> void setStateData(@Nonnull IBlockState state, @Nullable T data) {
      ((IDataAccessor)state).setData(data);
   }
}
