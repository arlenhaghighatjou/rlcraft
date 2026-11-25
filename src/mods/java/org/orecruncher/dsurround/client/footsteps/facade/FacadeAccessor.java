package org.orecruncher.dsurround.client.footsteps.facade;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;

@SideOnly(Side.CLIENT)
class FacadeAccessor implements IFacadeAccessor {
   protected Class<?> IFacadeClass;
   protected Method accessor;

   public FacadeAccessor(@Nonnull String clazz, @Nonnull String method) {
      try {
         this.IFacadeClass = Class.forName(clazz);
         this.accessor = this.getMethod(method);
      } catch (Throwable var4) {
         this.IFacadeClass = null;
         this.accessor = null;
      }

   }

   @Nonnull
   public String getName() {
      return this.isValid() ? this.IFacadeClass.getName() : "INVALID";
   }

   public boolean instanceOf(@Nonnull Block block) {
      return this.isValid() && this.IFacadeClass.isInstance(block);
   }

   public boolean isValid() {
      return this.accessor != null;
   }

   @Nullable
   public IBlockState getBlockState(@Nonnull EntityLivingBase entity, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull Vec3d pos, @Nullable EnumFacing side) {
      if (this.isValid()) {
         try {
            if (this.instanceOf(state.getBlock())) {
               return this.call(state, world, new BlockPos(pos), side);
            }
         } catch (Throwable var7) {
            ModBase.log().catching(var7);
            this.IFacadeClass = null;
            this.accessor = null;
         }
      }

      return null;
   }

   protected Method getMethod(@Nonnull String method) throws Throwable {
      return this.IFacadeClass.getMethod(method, IBlockAccess.class, BlockPos.class, EnumFacing.class);
   }

   protected IBlockState call(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) throws Throwable {
      return (IBlockState)this.accessor.invoke(state.getBlock(), world, pos, side);
   }
}
