package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.client.rendering.IRenderChunkSupplier;
import javax.vecmath.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeWorld extends World implements IOrientatedWorld {
   public Entity parent;
   @SideOnly(Side.CLIENT)
   public IRenderChunkSupplier renderChunkSupplier;
   public boolean hasChanged = false;
   public boolean preventNeighborUpdate = false;

   protected CreativeWorld(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
      super(saveHandlerIn, info, providerIn, profilerIn, client);
   }

   public void neighborChanged(BlockPos pos, final Block blockIn, BlockPos fromPos) {
      if (!this.preventNeighborUpdate) {
         if (this.isRemote) {
            IBlockState iblockstate = this.getBlockState(pos);

            try {
               iblockstate.neighborChanged(this, pos, blockIn, fromPos);
            } catch (Throwable var8) {
               CrashReport crashreport = CrashReport.makeCrashReport(var8, "Exception while updating neighbours");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
               crashreportcategory.addDetail("Source block type", new ICrashReportDetail<String>() {
                  public String call() throws Exception {
                     try {
                        return String.format("ID #%d (%s // %s // %s)", Block.getIdFromBlock(blockIn), blockIn.getUnlocalizedName(), blockIn.getClass().getName(), blockIn.getRegistryName());
                     } catch (Throwable var2) {
                        return "ID #" + Block.getIdFromBlock(blockIn);
                     }
                  }
               });
               CrashReportCategory.addBlockInfo(crashreportcategory, pos, iblockstate);
               throw new ReportedException(crashreport);
            }
         } else {
            super.neighborChanged(pos, blockIn, fromPos);
         }

      }
   }

   public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType, boolean updateObservers) {
      if (!this.preventNeighborUpdate) {
         super.notifyNeighborsOfStateChange(pos, blockType, updateObservers);
      }
   }

   public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide) {
      if (!this.preventNeighborUpdate) {
         super.notifyNeighborsOfStateExcept(pos, blockType, skipSide);
      }
   }

   public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType, boolean p_175722_3_) {
      if (!this.preventNeighborUpdate) {
         super.notifyNeighborsRespectDebug(pos, blockType, p_175722_3_);
      }
   }

   public BlockPos transformToRealWorld(BlockPos pos) {
      return new BlockPos(this.getOrigin().transformPointToWorld(new Vec3d(pos)));
   }

   public void playBroadcastSound(int id, BlockPos pos, int data) {
      if (this.getOrigin() != null) {
         this.getRealWorld().playBroadcastSound(id, this.transformToRealWorld(pos), data);
      }
   }

   public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
      if (this.getOrigin() != null) {
         Vector3d vec = new Vector3d(x, y, z);
         this.getOrigin().transformPointToWorld(vec);
         this.getRealWorld().playSound(vec.x, vec.y, vec.z, soundIn, category, volume, pitch, distanceDelay);
      }
   }

   public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      if (this.getOrigin() != null) {
         this.getRealWorld().playSound(player, this.transformToRealWorld(pos), soundIn, category, volume, pitch);
      }
   }

   public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
      if (this.getOrigin() != null) {
         Vector3d vec = new Vector3d(x, y, z);
         this.getOrigin().transformPointToWorld(vec);
         this.getRealWorld().playSound(player, x, y, z, soundIn, category, volume, pitch);
      }
   }

   public void playRecord(BlockPos blockPositionIn, SoundEvent soundEventIn) {
      if (this.getOrigin() != null) {
         this.getRealWorld().playRecord(this.transformToRealWorld(blockPositionIn), soundEventIn);
      }
   }
}
