package org.orecruncher.dsurround.client.footsteps.facade;

import com.creativemd.littletiles.common.api.te.ILittleTileTE;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.ReflectedField;

@SideOnly(Side.CLIENT)
final class LittleTilesAccessor implements IFacadeAccessor {
   private static final float RANGE = 0.125F;
   private static final Class<?> BLOCK_CLASS;

   LittleTilesAccessor() {
   }

   @Nonnull
   public String getName() {
      return "LittleTilesAccessor";
   }

   public boolean instanceOf(@Nonnull Block block) {
      return this.isValid() && BLOCK_CLASS.isInstance(block);
   }

   public boolean isValid() {
      return BLOCK_CLASS != null;
   }

   public IBlockState getBlockState(@Nonnull EntityLivingBase entity, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull Vec3d pos, @Nullable EnumFacing side) {
      BlockPos blockPos = new BlockPos(pos);
      TileEntity te = world.getTileEntity(blockPos);
      if (te instanceof ILittleTileTE) {
         ILittleTileTE ltte = (ILittleTileTE)te;
         Vec3d anchor1 = pos.addVector(-0.125D, 0.0625D, -0.125D);
         Vec3d anchor2 = pos.addVector(0.125D, 0.1875D, 0.125D);
         AxisAlignedBB box = new AxisAlignedBB(anchor1, anchor2);
         IBlockState result = ltte.getState(box, false);
         if (result != null) {
            return result;
         }
      }

      return state;
   }

   static {
      Class<?> theClass = ReflectedField.resolveClass("com.creativemd.littletiles.common.blocks.BlockTile");
      if (theClass == null) {
         theClass = ReflectedField.resolveClass("com.creativemd.littletiles.common.block.BlockTile");
      }

      BLOCK_CLASS = theClass;
   }
}
