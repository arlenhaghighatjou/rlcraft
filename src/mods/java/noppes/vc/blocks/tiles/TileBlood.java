package noppes.vc.blocks.tiles;

import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class TileBlood extends TileEntity {
   public boolean hideTop = true;
   public boolean hideBottom = true;
   public boolean hideNorth = true;
   public boolean hideSouth = true;
   public boolean hideEast = true;
   public boolean hideWest = true;
   public int rotation = 0;

   public TileBlood() {
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.hideTop = compound.getBoolean("HideTop");
      this.hideBottom = compound.getBoolean("HideBottom");
      this.hideNorth = compound.getBoolean("HideNorth");
      this.hideSouth = compound.getBoolean("HideSouth");
      this.hideEast = compound.getBoolean("HideEast");
      this.hideWest = compound.getBoolean("HideWest");
      this.rotation = compound.getInteger("Rotation");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setBoolean("HideTop", this.hideTop);
      compound.setBoolean("HideBottom", this.hideBottom);
      compound.setBoolean("HideNorth", this.hideNorth);
      compound.setBoolean("HideSouth", this.hideSouth);
      compound.setBoolean("HideEast", this.hideEast);
      compound.setBoolean("HideWest", this.hideWest);
      compound.setInteger("Rotation", this.rotation);
      return super.writeToNBT(compound);
   }

   public boolean isFullBlock() {
      int i = 0;
      if (this.hideTop) {
         ++i;
      }

      if (this.hideBottom) {
         ++i;
      }

      if (this.hideNorth) {
         ++i;
      }

      if (this.hideSouth) {
         ++i;
      }

      if (this.hideEast) {
         ++i;
      }

      if (this.hideWest) {
         ++i;
      }

      return i == 0 || i > 1;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.getUpdateTag());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeToNBT(compound);
      compound.removeTag("ExtraData");
      return compound;
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound compound = pkt.getNbtCompound();
      this.readFromNBT(compound);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return new AxisAlignedBB((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1));
   }
}
