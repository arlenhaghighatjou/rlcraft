package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileBasicRotation extends TileEntity {
   public int color = 14;
   public int rotation;

   public TileBasicRotation() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.color = compound.getInteger("BannerColor");
      this.rotation = compound.getInteger("BannerRotation");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("BannerColor", this.color);
      compound.setInteger("BannerRotation", this.rotation);
      return super.writeToNBT(compound);
   }

   public boolean canUpdate() {
      return false;
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound compound = pkt.getNbtCompound();
      this.readFromNBT(compound);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeToNBT(compound);
      compound.removeTag("Items");
      compound.removeTag("ExtraData");
      return compound;
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1));
   }

   public int powerProvided() {
      return 0;
   }
}
