package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.tools.EnumFacingHelper;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class IvTileEntityMultiBlock extends TileEntity {
   public BlockPos parentCoords;
   public BlockPos[] childCoords;
   public EnumFacing facing;
   public double[] centerCoords = new double[]{0.5D, 0.5D, 0.5D};
   public double[] centerCoordsSize = new double[]{0.5D, 0.5D, 0.5D};
   public boolean multiblockInvalid;

   public IvTileEntityMultiBlock() {
   }

   public BlockPos getActiveParentCoords() {
      return this.parentCoords == null ? this.getPos() : this.getPos().add(this.parentCoords.getX(), this.parentCoords.getY(), this.parentCoords.getZ());
   }

   public BlockPos[] getActiveChildCoords() {
      if (this.childCoords == null) {
         return new BlockPos[0];
      } else {
         BlockPos[] returnVal = new BlockPos[this.childCoords.length];

         for(int i = 0; i < returnVal.length; ++i) {
            returnVal[i] = this.getPos().add(this.childCoords[i]);
         }

         return returnVal;
      }
   }

   public double[] getActiveCenterCoords() {
      return this.centerCoords == null ? new double[]{(double)this.getPos().getX() + 0.5D, (double)this.getPos().getY() + 0.5D, (double)this.getPos().getY() + 0.5D} : new double[]{(double)this.getPos().getX() + this.centerCoords[0], (double)this.getPos().getY() + this.centerCoords[1], (double)this.getPos().getZ() + this.centerCoords[2]};
   }

   public void becomeChild(TileEntity parent) {
      if (parent != null) {
         this.parentCoords = BlockPositions.sub(parent.getPos(), this.getPos());
      } else {
         this.parentCoords = null;
      }

   }

   public void becomeParent(List<BlockPos> childCoords) {
      if (childCoords != null) {
         this.childCoords = new BlockPos[childCoords.size()];

         for(int i = 0; i < childCoords.size(); ++i) {
            this.childCoords[i] = BlockPositions.sub((BlockPos)childCoords.get(i), this.getPos());
         }
      } else {
         this.childCoords = new BlockPos[0];
      }

   }

   public boolean isParent() {
      return this.parentCoords == null;
   }

   public IvTileEntityMultiBlock getTotalParent() {
      return this.isParent() ? this : this.getParent();
   }

   public IvTileEntityMultiBlock getParent() {
      if (this.parentCoords != null) {
         TileEntity parentTileEntity = this.world.getTileEntity(this.getActiveParentCoords());
         if (parentTileEntity != null && this.getClass().equals(parentTileEntity.getClass())) {
            return (IvTileEntityMultiBlock)parentTileEntity;
         }
      }

      return null;
   }

   public float getDistanceToCenter() {
      return (float)IvVecMathHelper.distance(new double[]{(double)this.getPos().getX() + 0.5D, (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ() + 0.5D}, this.getActiveCenterCoords());
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      super.readFromNBT(tagCompound);
      if (tagCompound.hasKey("direction", 3)) {
         switch(tagCompound.getInteger("direction")) {
         case 0:
            this.facing = EnumFacing.SOUTH;
         case 1:
            this.facing = EnumFacing.WEST;
         case 2:
            this.facing = EnumFacing.NORTH;
         case 3:
            this.facing = EnumFacing.EAST;
         default:
            this.facing = EnumFacing.SOUTH;
         }
      } else {
         this.facing = EnumFacingHelper.byName(tagCompound.getString("facing"), EnumFacing.SOUTH);
      }

      this.centerCoords = IvNBTHelper.readDoubleArray("multiBlockCenter", tagCompound);
      if (this.centerCoords == null) {
         this.centerCoords = new double[3];
         if (tagCompound.hasKey("centerCoords[0]")) {
            this.centerCoords[0] = tagCompound.getDouble("centerCoords[0]") - (double)this.getPos().getX();
         } else {
            this.centerCoords[0] = 0.5D;
         }

         if (tagCompound.hasKey("centerCoords[1]")) {
            this.centerCoords[1] = tagCompound.getDouble("centerCoords[1]") - (double)this.getPos().getY();
         } else {
            this.centerCoords[1] = 0.5D;
         }

         if (tagCompound.hasKey("centerCoords[2]")) {
            this.centerCoords[2] = tagCompound.getDouble("centerCoords[2]") - (double)this.getPos().getZ();
         } else {
            this.centerCoords[2] = 0.5D;
         }
      }

      this.centerCoordsSize = IvNBTHelper.readDoubleArray("multiBlockSize", tagCompound);
      if (this.centerCoordsSize == null) {
         this.centerCoordsSize = new double[3];
         if (tagCompound.hasKey("centerCoordsSize[0]")) {
            this.centerCoordsSize[0] = (double)tagCompound.getFloat("centerCoordsSize[0]");
         } else {
            this.centerCoordsSize[0] = 0.5D;
         }

         if (tagCompound.hasKey("centerCoordsSize[1]")) {
            this.centerCoordsSize[1] = (double)tagCompound.getFloat("centerCoordsSize[1]");
         } else {
            this.centerCoordsSize[1] = 0.5D;
         }

         if (tagCompound.hasKey("centerCoordsSize[2]")) {
            this.centerCoordsSize[2] = (double)tagCompound.getFloat("centerCoordsSize[2]");
         } else {
            this.centerCoordsSize[2] = 0.5D;
         }
      }

      if (tagCompound.hasKey("parentCoords")) {
         this.parentCoords = BlockPositions.fromIntArray(IvNBTHelper.readIntArrayFixedSize("parentCoords", 3, tagCompound));
      } else {
         this.parentCoords = null;
      }

      if (tagCompound.hasKey("childCoords")) {
         int[] childCoordsCut = tagCompound.getIntArray("childCoords");
         this.childCoords = new BlockPos[childCoordsCut.length / 3];

         for(int i = 0; i < this.childCoords.length; ++i) {
            this.childCoords[i] = new BlockPos(childCoordsCut[i * 3], childCoordsCut[i * 3 + 1], childCoordsCut[i * 3 + 2]);
         }
      } else if (this.parentCoords == null) {
         this.childCoords = new BlockPos[0];
      }

      this.multiblockInvalid = tagCompound.getBoolean("multiblockInvalid");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setString("facing", this.facing.getName());
      IvNBTHelper.writeDoubleArray("multiBlockCenter", this.centerCoords, tagCompound);
      IvNBTHelper.writeDoubleArray("multiBlockSize", this.centerCoordsSize, tagCompound);
      if (this.parentCoords != null) {
         tagCompound.setIntArray("parentCoords", BlockPositions.toIntArray(this.parentCoords));
      }

      if (this.childCoords != null) {
         int[] childCoordsCut = new int[this.childCoords.length * 3];

         for(int i = 0; i < this.childCoords.length; ++i) {
            childCoordsCut[i * 3] = this.childCoords[i].getX();
            childCoordsCut[i * 3 + 1] = this.childCoords[i].getY();
            childCoordsCut[i * 3 + 2] = this.childCoords[i].getZ();
         }

         tagCompound.setIntArray("childCoords", childCoordsCut);
      }

      return tagCompound;
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return IvTileEntityHelper.getStandardDescriptionPacket(this);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public IvRaytraceableAxisAlignedBox getInterpolatedRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, double x1, double y1, double z1, double width1, double height1, double depth1, float fraction) {
      double xI = IvMathHelper.mix(x, x1, (double)fraction);
      double yI = IvMathHelper.mix(y, y1, (double)fraction);
      double zI = IvMathHelper.mix(z, z1, (double)fraction);
      double wI = IvMathHelper.mix(width, width1, (double)fraction);
      double hI = IvMathHelper.mix(height, height1, (double)fraction);
      double dI = IvMathHelper.mix(depth, depth1, (double)fraction);
      return this.getRotatedBox(userInfo, xI, yI, zI, wI, hI, dI);
   }

   public IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth) {
      return IvMultiBlockHelper.getRotatedBox(userInfo, x, y, z, width, height, depth, this.getFacing(), this.getActiveCenterCoords());
   }

   public AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth) {
      return IvMultiBlockHelper.getRotatedBB(x, y, z, width, height, depth, this.getFacing(), this.getActiveCenterCoords());
   }

   public Vector3f getRotatedVector(Vector3f vector3f) {
      return IvMultiBlockHelper.getRotatedVector(vector3f, this.getFacing());
   }

   public Vec3d getRotatedVector(Vec3d vec3) {
      return IvMultiBlockHelper.getRotatedVector(vec3, this.getFacing());
   }

   public EnumFacing getFacing() {
      return this.facing;
   }

   public AxisAlignedBB getBoxAroundCenter(double width, double height, double length) {
      double[] center = this.getActiveCenterCoords();
      return new AxisAlignedBB(center[0] - width, center[1] - height, center[2] - length, center[0] + width, center[1] + height, center[2] + length);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return !this.isParent() ? this.getBoxAroundCenter(0.0D, 0.0D, 0.0D) : this.getBoxAroundCenter(this.centerCoordsSize[0], this.centerCoordsSize[1], this.centerCoordsSize[2]);
   }
}
