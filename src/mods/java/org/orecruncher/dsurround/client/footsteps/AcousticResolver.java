package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.footsteps.facade.FacadeHelper;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.footstep.BlockMap;
import org.orecruncher.dsurround.registry.footstep.Substrate;
import org.orecruncher.lib.MyUtils;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class AcousticResolver {
   protected final IBlockState airState = Blocks.AIR.getDefaultState();
   protected final IBlockAccess world;
   protected final BlockMap blockMap;
   protected final FootStrikeLocation loc;
   protected final double distanceToCenter;

   public AcousticResolver(@Nonnull IBlockAccess world, @Nonnull BlockMap map, @Nonnull FootStrikeLocation loc, double distanceToCenter) {
      this.world = world;
      this.blockMap = map;
      this.loc = loc;
      this.distanceToCenter = distanceToCenter;
   }

   protected IBlockState getBlockStateFacade(@Nonnull Vec3d pos) {
      return FacadeHelper.resolveState(this.loc.getEntity(), this.getBlockState(pos), this.world, pos, EnumFacing.UP);
   }

   protected IBlockState getBlockState(@Nonnull Vec3d pos) {
      return this.world.getBlockState(new BlockPos(pos));
   }

   @Nullable
   public Association findAssociationForEvent() {
      Vec3d pos = this.loc.getStrikePosition();
      Association worked = this.resolve(pos);
      if (worked == null) {
         EntityLivingBase entity = this.loc.getEntity();
         BlockPos adj = new BlockPos(pos);
         double xdang = (entity.posX - (double)adj.getX()) * 2.0D - 1.0D;
         double zdang = (entity.posZ - (double)adj.getZ()) * 2.0D - 1.0D;
         if (Math.max(MathStuff.abs(xdang), MathStuff.abs(zdang)) > this.distanceToCenter) {
            boolean isXdangMax = MathStuff.abs(xdang) > MathStuff.abs(zdang);
            if (isXdangMax) {
               worked = this.resolve(xdang > 0.0D ? this.loc.east() : this.loc.west());
            } else {
               worked = this.resolve(zdang > 0.0D ? this.loc.south() : this.loc.north());
            }

            if (worked == null) {
               if (isXdangMax) {
                  worked = this.resolve(zdang > 0.0D ? this.loc.south() : this.loc.north());
               } else {
                  worked = this.resolve(xdang > 0.0D ? this.loc.east() : this.loc.west());
               }
            }
         }
      }

      return worked;
   }

   @Nullable
   protected Association resolve(@Nonnull Vec3d vec) {
      IAcoustic[] acoustics = AcousticRegistry.EMPTY;
      Vec3d tPos = vec.addVector(0.0D, 1.0D, 0.0D);
      IBlockState above = this.getBlockState(tPos);
      if (above != this.airState) {
         acoustics = this.blockMap.getBlockAcoustics(above, Substrate.CARPET);
      }

      IBlockState in;
      if (acoustics != AcousticRegistry.NOT_EMITTER && acoustics != AcousticRegistry.EMPTY) {
         vec = tPos;
         in = above;
      } else {
         in = this.getBlockStateFacade(vec);
         if (in == this.airState) {
            tPos = vec.addVector(0.0D, -1.0D, 0.0D);
            IBlockState below = this.getBlockState(tPos);
            acoustics = this.blockMap.getBlockAcoustics(below, Substrate.FENCE);
            if (acoustics != AcousticRegistry.EMPTY) {
               vec = tPos;
               in = below;
            }
         }

         if (acoustics == AcousticRegistry.EMPTY) {
            acoustics = this.blockMap.getBlockAcoustics(in);
         }

         if (acoustics != AcousticRegistry.NOT_EMITTER && above != this.airState) {
            IAcoustic[] foliage = this.blockMap.getBlockAcoustics(above, Substrate.FOLIAGE);
            if (foliage != AcousticRegistry.NOT_EMITTER) {
               acoustics = (IAcoustic[])MyUtils.concatenate(acoustics, foliage);
            }
         }
      }

      return acoustics == AcousticRegistry.NOT_EMITTER ? null : new Association(in, this.loc.rebase(new BlockPos(vec)), acoustics);
   }
}
