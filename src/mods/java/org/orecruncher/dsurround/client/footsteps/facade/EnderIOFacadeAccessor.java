package org.orecruncher.dsurround.client.footsteps.facade;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class EnderIOFacadeAccessor extends FacadeAccessor {
   private static final String CLASS = "crazypants.enderio.paint.IPaintable";
   private static final String METHOD = "getPaintSource";

   public EnderIOFacadeAccessor() {
      super("crazypants.enderio.paint.IPaintable", "getPaintSource");
   }

   protected Method getMethod(@Nonnull String method) throws Throwable {
      return this.IFacadeClass.getMethod(method, IBlockState.class, IBlockAccess.class, BlockPos.class);
   }

   protected IBlockState call(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) throws Throwable {
      return (IBlockState)this.accessor.invoke(state.getBlock(), state, world, pos);
   }
}
