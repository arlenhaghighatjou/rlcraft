package org.orecruncher.dsurround.client.footsteps.facade;

import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.Microblock;
import codechicken.multipart.BlockMultipart;
import codechicken.multipart.PartRayTraceResult;
import codechicken.multipart.TileMultipart;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class ForgeMultiPartCBE implements IFacadeAccessor {
   public ForgeMultiPartCBE() {
   }

   @Nonnull
   public String getName() {
      return "ForgeMultiPartCBE";
   }

   public boolean instanceOf(@Nonnull Block block) {
      return this.isValid() && block instanceof BlockMultipart;
   }

   public boolean isValid() {
      return true;
   }

   public IBlockState getBlockState(@Nonnull EntityLivingBase entity, @Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull Vec3d vec, @Nullable EnumFacing side) {
      try {
         BlockPos pos = new BlockPos(vec);
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileMultipart) {
            TileMultipart tm = (TileMultipart)te;
            Vec3d end = vec.addVector(0.0D, -1.0D, 0.0D);
            PartRayTraceResult result = tm.collisionRayTrace(vec, end);
            if (result != null && result.typeOfHit == Type.BLOCK) {
               Microblock micro = (Microblock)tm.partList().apply(result.partIndex());
               if (micro != null) {
                  BlockMicroMaterial bmm = (BlockMicroMaterial)micro.getIMaterial();
                  if (bmm != null) {
                     IBlockState mmState = bmm.state();
                     if (mmState != null) {
                        return mmState;
                     }
                  }
               }
            }
         }
      } catch (Throwable var14) {
         ;
      }

      return state;
   }
}
