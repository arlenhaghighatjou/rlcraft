package org.orecruncher.dsurround.client.footsteps.facade;

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
import team.chisel.ctm.api.IFacade;

@SideOnly(Side.CLIENT)
final class ConnectedTexturesAccessor implements IFacadeAccessor {
   ConnectedTexturesAccessor() {
   }

   @Nonnull
   public String getName() {
      return "ConnectedTexturesAccessor";
   }

   public boolean instanceOf(@Nonnull Block block) {
      return this.isValid() && block instanceof IFacade;
   }

   public boolean isValid() {
      return true;
   }

   @Nonnull
   public IBlockState getBlockState(@Nonnull EntityLivingBase entity, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull Vec3d pos, @Nullable EnumFacing side) {
      if (state.getBlock() instanceof IFacade) {
         IFacade facade = (IFacade)state.getBlock();
         BlockPos blockPos = new BlockPos(pos);
         return facade.getFacade(entity.getEntityWorld(), blockPos, side, blockPos);
      } else {
         return state;
      }
   }
}
