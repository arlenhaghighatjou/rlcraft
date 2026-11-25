package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.tools.EnumFacingHelper;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector3f;

public class IvTileEntityRotatable extends TileEntity {
   public EnumFacing facing;

   public IvTileEntityRotatable() {
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

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
      super.writeToNBT(tagCompound);
      tagCompound.setString("facing", this.facing.getName());
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
      return IvMultiBlockHelper.getRotatedBox(userInfo, x, y, z, width, height, depth, this.getFacing(), new double[]{(double)this.getPos().getX() + 0.5D, (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ() + 0.5D});
   }

   public AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth) {
      return IvMultiBlockHelper.getRotatedBB(x, y, z, width, height, depth, this.getFacing(), new double[]{(double)this.getPos().getX() + 0.5D, (double)this.getPos().getY() + 0.5D, (double)this.getPos().getZ() + 0.5D});
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
}
